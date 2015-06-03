package il.ac.technion.cs.sd.lib.communication;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.msg.Messenger;
import il.ac.technion.cs.sd.msg.MessengerException;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class MessageConsumer implements BiConsumer<Messenger, String>{

	private static final long TIMEOUT_FOR_ACK_IN_MILLIES = 500;

	private LinkedBlockingQueue<Message> outgoingMessages;
	
	private LinkedBlockingQueue<Message> incomingMessages;
	
	private Consumer<String> appConsumer;
	
	private String myAddress;

	
	public MessageConsumer(Consumer<String> consumer, String address) {
		
		appConsumer = consumer;
		incomingMessages = new LinkedBlockingQueue<Message>();
		outgoingMessages = new LinkedBlockingQueue<Message>();
		myAddress = address;
	}
	
	@Override
	public void accept(Messenger messenger, String data) {

		Message message = (Message) StringConverter.convertFromString(data);
		if(message.data.isEmpty()){
			System.err.println("received empty message. shouldn't be ack");
			return;
		}
		// send Ack
		sendAck(messenger, message);
		//deosnsdf
		
		// send message to the app consumer.
		appConsumer.accept(message.data);
	}

	private void sendAck(Messenger messenger, Message message) {
		Message ack = new Message(myAddress, "");
		String ackAsString = StringConverter.convertToString(ack);
		try {
			messenger.send(message.from, ackAsString);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}
	
	public void sendMessage(Message messageToSend, String to, Messenger messenger) throws InterruptedException, MessengerException {

		if(!outgoingMessages.isEmpty()){
			System.out.println("trying to send message but still has message to send");
			outgoingMessages.add(messageToSend);
		}
		// send message and wait for Ack
		while(true){
			messenger.send(to, StringConverter.convertToString(messageToSend));
			String messageReceivedAsString = messenger.getNextMessage(TIMEOUT_FOR_ACK_IN_MILLIES); 

			if( messageReceivedAsString == null){
				System.out.println("no ack returned yet.");
				continue;
			}
			Message messageReceived = (Message) StringConverter.convertFromString(messageReceivedAsString);
			// received Ack
			if(messageReceived.data.isEmpty()){
				break;
			// received a message from the server.
			}else{
				System.out.println("waited for ack but received a different message.");
				incomingMessages.add(messageReceived);
				sendAck(messenger, messageReceived);
			}
		}
		
		handleIncomingMessages(messenger);
		
	}

	private void handleIncomingMessages(Messenger messenger) throws InterruptedException {

		while(!incomingMessages.isEmpty()){

			Message incomingMessage = incomingMessages.take();
			
			appConsumer.accept(incomingMessage.data);
		}
	}
}

