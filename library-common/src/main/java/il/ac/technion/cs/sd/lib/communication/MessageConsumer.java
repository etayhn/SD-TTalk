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
 * This class represents a message consumer responsible for sending and receiving messages.
 * @author Avner
 *
 */
public class MessageConsumer implements BiConsumer<Messenger, String>{

	private static final String ACK = "";

	private static final long TIMEOUT_FOR_ACK_IN_MILLIES = 100;

	private LinkedBlockingQueue<Message> incomingMessages;
	private LinkedBlockingQueue<String> ackMessages;
	
	private Consumer<Object> appConsumer;
	private Map<String, Message> lastMessageReceived;
	private String myAddress;
	private boolean waitingForAck;

	public MessageConsumer(Consumer<Object> consumer, String address) {
		
		appConsumer = consumer;
		incomingMessages = new LinkedBlockingQueue<Message>();
		ackMessages =  new LinkedBlockingQueue<String>();
		myAddress = address;
		
		lastMessageReceived = new ConcurrentHashMap<String, Message>();
	}
	
	@Override
	public void accept(Messenger messenger, String data) {

		//received Ack
		if(data.isEmpty()){
//			System.err.println("address: " + myAddress + 
//					", received empty message. shouldn't be ack");
			
			System.out.println("address: " + myAddress + ", received Ack");
			ackMessages.add(data);
			return;
		}
		Message message = (Message) StringConverter.convertFromString(data);
		System.out.println("address: " + myAddress + ", received message: " + message);		

		// other side has stopped
		if(checkMessageAndSendAck(messenger, message)){

			if(message.isStoppedMessage()){
				
				System.out.println("address: " + myAddress + ", received stop: ");		
				lastMessageReceived.remove(message.from);
				return;
			}

			if(!waitingForAck){

				System.out.println("address: " + myAddress + ", uploading message: " + message + " to consumer");		
				// send message to the app consumer.
//				new Thread(()->appConsumer.accept(StringConverter.convertFromString(message.data))).start();
				appConsumer.accept(StringConverter.convertFromString(message.data));
				
			// deal with message after receiving ack.
			}else{
				System.out.println("address: " + myAddress + ", put message: " + message + " in queue");
				incomingMessages.add(message);
			}
		}
	}

	private boolean checkMessageAndSendAck(Messenger messenger, Message message) {
		Message lastMessageFromAddress = lastMessageReceived.get(message.from);
		
		// ignore message if it was already received.
		if(lastMessageFromAddress!= null && lastMessageFromAddress.counter == message.counter){
			System.err.println("address: " + myAddress +", message already received: "
					+ message + ". counter should be bigger than: " 
					+ lastMessageFromAddress.counter);
			return false;
		}
		lastMessageReceived.put(message.from, message);
		// send Ack
		sendAck(messenger, message);
		return true;
	}

	private void sendAck(Messenger messenger, Message message) {
		try {
			System.out.println("address: " + myAddress + 
					", sent Ack. counter: " + message.counter + ", to: " + message.from);
			messenger.send(message.from, ACK);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}
	
	public void sendMessage(Message messageToSend , String to, Messenger messenger) throws InterruptedException, MessengerException {

		String messgeAsString = StringConverter.convertToString(messageToSend);
		
		// send message and wait for Ack
		boolean ackRecieved = false;
		while(!ackRecieved){
			messenger.send(to, messgeAsString);
			System.out.println("address: " + myAddress + ", sent message: " + messageToSend 
																			+ ", to: " + to);
			ackRecieved = waitForAck(messenger);
		}
		handleIncomingMessages(messenger);
		
	}

	private boolean waitForAck(Messenger messenger) throws InterruptedException {
//		System.out.println("address: " + myAddress + " waiting for ack"); 
//		String messageReceivedAsString = messenger.getLastOrNextMessage(TIMEOUT_FOR_ACK_IN_MILLIES); 
//
//		if( messageReceivedAsString == null){
//			System.out.println("address: " + myAddress + " waited for ack but received nothing");
//			
//			return false;
//		}
		// received Ack
//		if(messageReceivedAsString.isEmpty()){
//			System.out.println("address: " + myAddress + ", received ack ");
//			return true;
//			
//		// received a different message instead of Ack.
//		}else{
//			Message messageReceived = (Message) StringConverter.convertFromString(messageReceivedAsString);
//			System.out.println("address: " + myAddress + 
//					", waited for ack but received a different message: " + messageReceived);
//			if(checkMessageAndSendAck(messenger, messageReceived)){
//				
//				incomingMessages.add(messageReceived);
//			}
//			// keep waiting for ack.
//			return false;
//		}
		waitingForAck = true;
		
		String messageReceivedAsString = messenger.getLastOrNextMessage(TIMEOUT_FOR_ACK_IN_MILLIES); 

		if(messageReceivedAsString!= null){
			System.out.println("address: " + myAddress + 
			", waited explicitly for message");

			accept(messenger, messageReceivedAsString);
		}
		String ack = ackMessages.poll(TIMEOUT_FOR_ACK_IN_MILLIES, TimeUnit.MILLISECONDS);
		if(ack == null){
			return false;
		}else{
			waitingForAck = false;
			return true;
		}
	}

	private void handleIncomingMessages(Messenger messenger) throws InterruptedException {

		while(!incomingMessages.isEmpty()){

			Message incomingMessage = incomingMessages.take();

			// send message to the app consumer.
			appConsumer.accept(StringConverter.convertFromString(incomingMessage.data));
//			new Thread(()->appConsumer.accept(StringConverter.convertFromString(incomingMessage.data))).start();

		}
	}
}

