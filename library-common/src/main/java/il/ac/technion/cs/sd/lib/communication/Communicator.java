package il.ac.technion.cs.sd.lib.communication;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.msg.Messenger;
import il.ac.technion.cs.sd.msg.MessengerException;
import il.ac.technion.cs.sd.msg.MessengerFactory;

import java.util.function.Consumer;

/**
 * This is an abstract class that represents a communicator that can send and receive message. 
 * @author avner
 *
 */
public abstract class Communicator {
	/**
	 * The messenger object with which we send/receive the data
	 */
	protected Messenger messenger;

	/**
	 * Address of the current communicator.
	 */
	protected String myAddress;

	/**
	 * represents whether this communicator has already been closed.
	 */
	protected boolean isCommunicatorClosed;
	
	/**
	 * consumer for incoming and outgoing messages.
	 */
	protected MessageConsumer messageConsumer;
	
	/**
	 * counter for messages sent by this communicator.
	 */
	protected int messageCounter;

	/**
	 * builds a new communicator. It will start receiving new messages. 
	 * @param myAddress - address of current communicator.
	 * @param consumer - consumer for messages that will be received by this communicator.
	 */
	public Communicator(String myAddress, Consumer<Object> consumer) {
		if (myAddress == null || consumer == null)
			throw new IllegalArgumentException("myAddress cannot be null");

		this.myAddress = myAddress;
		isCommunicatorClosed = false;
		
		messageConsumer = new MessageConsumer(consumer, myAddress);
		try {
			messenger = new MessengerFactory().start(myAddress, messageConsumer);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}

	/**
	 * This method returns if the communicator has been stopped.
	 * @return true if the communicator has been stopped. false otherwise.
	 */
	public boolean isCommunicatorStopped() {
		return isCommunicatorClosed;
	}

	/**
	 * Stops the communicator. Any function invocation after this call will
	 * fail.
	 */
	public void stop() {
		checkLiveness();

		try {
			messenger.kill();
			isCommunicatorClosed = true;
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}

	private void checkLiveness() {
		if (isCommunicatorClosed) {
			throw new RuntimeException(
					"Communicator is dead. Cannot activate its methods.");
		}
	}

	/**
	 * Sends (in a blocking manner) an object to a given address. The message is guaranteed 
	 * to arrive to the recipient (assuming it has already been initialized).
	 * 
	 * @param to
	 *            the address of the recipient
	 * @param data
	 *            the data to send
	 */
	protected void send(String to, Object data) {
		checkLiveness();
		if (to == null)
			throw new IllegalArgumentException("address cannot be null");
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");

		String dataAsString = StringConverter.convertToString(data);
		Message messageToSend = new Message(myAddress, dataAsString, messageCounter++);
		try {
			messageConsumer.sendMessage(messageToSend, to, messenger);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
