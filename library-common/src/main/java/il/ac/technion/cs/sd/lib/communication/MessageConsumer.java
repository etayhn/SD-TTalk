package il.ac.technion.cs.sd.lib.communication;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.msg.Messenger;
import il.ac.technion.cs.sd.msg.MessengerException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * This class represents a message consumer responsible for sending and
 * receiving messages.
 * 
 * @author Avner
 *
 */
public class MessageConsumer implements BiConsumer<Messenger, String> {

	private static final String ACK = "";
	private static final long TIMEOUT_FOR_ACK_IN_MILLIES = 100;

	/**
	 * queue that holds all incoming messages received while waiting for ack
	 */
	private LinkedBlockingQueue<Message> incomingMessages;

	/**
	 * queue that holds ack message received. this will be used to check when an
	 * ack is received.
	 */
	private LinkedBlockingQueue<String> ackMessages;

	/**
	 * consumer to call with incoming messages.
	 */
	private Consumer<Object> appConsumer;

	/**
	 * map that holds last message received from other communicators. this is
	 * used to check counters of messages and ignore messages that have already
	 * been accepted.
	 */
	private Map<String, Message> lastMessageReceived;

	/**
	 * address of the communicator that holds this consumer.
	 */
	private String myAddress;

	/**
	 * indicates whether this consumer is currently waiting for ack.
	 */
	private boolean waitingForAck;

	/**
	 * 
	 * @param consumer
	 *            consumer to be activated when a new message is received.
	 * @param address
	 *            address of the communicator that holds this consumer.
	 */
	public MessageConsumer(Consumer<Object> consumer, String address) {

		appConsumer = consumer;
		incomingMessages = new LinkedBlockingQueue<Message>();
		ackMessages = new LinkedBlockingQueue<String>();
		myAddress = address;
		lastMessageReceived = new ConcurrentHashMap<String, Message>();
	}

	/**
	 * accepts a new message from the messenger. message is validated (using
	 * counter) and handled according to its type.
	 */
	@Override
	public void accept(Messenger messenger, String data) {

		if(messenger == null || data == null){
			throw new IllegalArgumentException("null parameters for method accept");
		}
		// received Ack
		if (data.isEmpty()) {

			Communicator.logger.debug("address: " + myAddress
					+ ", received Ack");
			ackMessages.add(data);
			return;
		}
		Message message = (Message) StringConverter.convertFromString(data);
		Communicator.logger.debug("address: " + myAddress
				+ ", received message: " + message);

		// message's counter is vaild and it shouldn't be ignored.
		if (isValidMessage(messenger, message)) {

			// other side has stopped
			if (message.isStoppedMessage()) {
				Communicator.logger.debug("address: " + myAddress
						+ ", received stop: ");
				lastMessageReceived.remove(message.from);
				return;
			}

			// send message to the app consumer.
			if (!waitingForAck) {
				Communicator.logger.debug("address: " + myAddress
						+ ", uploading message: " + message + " to consumer");
				appConsumer.accept(StringConverter
						.convertFromString(message.data));

				// deal with message after receiving ack. save for now.
			} else {
				Communicator.logger.debug("address: " + myAddress
						+ ", put message: " + message + " in queue");
				incomingMessages.add(message);
			}
		}
	}

	/**
	 * checks validity of message using it's counter. if a message is valid then
	 * an ack is returned to the source.
	 * 
	 * @param messenger
	 *            messenger for sending the ack.
	 * @param message
	 *            message to validate.
	 * @return true if message is valid. false otherwise.
	 */
	private boolean isValidMessage(Messenger messenger, Message message) {

		Message lastMessageFromAddress = lastMessageReceived.get(message.from);
		// ignore message if it was already received.
		if (lastMessageFromAddress != null
				&& lastMessageFromAddress.counter == message.counter) {
			Communicator.logger.error("address: " + myAddress
					+ ", message already received: " + message
					+ ". counter should be bigger than: "
					+ lastMessageFromAddress.counter);
			return false;
		}
		// save msg.
		lastMessageReceived.put(message.from, message);
		sendAck(messenger, message);
		return true;
	}

	/**
	 * This method sends an Ack message to the source of message.
	 * 
	 * @param messenger
	 *            messenger to send the ack with.
	 * @param message
	 *            message to send ack for.
	 */
	private void sendAck(Messenger messenger, Message message) {
		try {
			Communicator.logger.debug("address: " + myAddress
					+ ", sent Ack. counter: " + message.counter + ", to: "
					+ message.from);
			messenger.send(message.from, ACK);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}

	/**
	 * This method sends a message to the recipient stated in param to. after
	 * sending the message it blocks until it receives an Ack for the message.
	 * 
	 * @param messageToSend
	 *            message to send to recipient.
	 * @param to
	 *            address of the recipient.
	 * @param messenger
	 *            messenger used to send the message.
	 * @throws InterruptedException
	 * @throws MessengerException
	 */
	public void sendMessage(Message messageToSend, String to,
			Messenger messenger) throws InterruptedException,
			MessengerException {
		
		if(messenger == null || messageToSend == null || to == null){
			throw new IllegalArgumentException("null parameters for method sendMessage");
		}

		String messgeAsString = StringConverter.convertToString(messageToSend);

		// send message and wait for Ack
		boolean ackRecieved = false;
		while (!ackRecieved) {
			messenger.send(to, messgeAsString);
			Communicator.logger.debug("address: " + myAddress
					+ ", sent message: " + messageToSend + ", to: " + to);
			ackRecieved = waitForAck(messenger);
		}
		handleIncomingMessages();

	}

	/**
	 * This method waits for an Ack message for a message that was sent.
	 * 
	 * @param messenger
	 *            messenger to retrieve ack from.
	 * @return true if ack was received. false otherwise.
	 * @throws InterruptedException
	 */
	private boolean waitForAck(Messenger messenger) throws InterruptedException {
		waitingForAck = true;

		// try to receive an ack from the messenger.
		String messageReceivedAsString = messenger
				.getLastOrNextMessage(TIMEOUT_FOR_ACK_IN_MILLIES);

		if (messageReceivedAsString != null) {
			Communicator.logger.debug("address: " + myAddress
					+ ", waited explicitly for message");
			accept(messenger, messageReceivedAsString);
		}
		
		String ack = ackMessages.poll(TIMEOUT_FOR_ACK_IN_MILLIES,
				TimeUnit.MILLISECONDS);
		if (ack == null) {
			return false;
		} else {
			waitingForAck = false;
			return true;
		}
	}

	/**
	 * This method handles all incoming messages that were received while waiting for ack.
	 * @throws InterruptedException
	 */
	private void handleIncomingMessages() throws InterruptedException {

		while (!incomingMessages.isEmpty()) {

			Message incomingMessage = incomingMessages.take();
			// send message to the app consumer.
			appConsumer.accept(StringConverter.convertFromString(incomingMessage.data));
		}
	}
}
