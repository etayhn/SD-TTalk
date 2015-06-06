package il.ac.technion.cs.sd.lib.client.communication;

import java.util.function.Consumer;

import il.ac.technion.cs.sd.lib.communication.Communicator;
import il.ac.technion.cs.sd.lib.communication.Message;
import il.ac.technion.cs.sd.msg.MessengerException;


public class ClientCommunicator extends Communicator{

	String serverAddress;
	public ClientCommunicator(String myAddress, String serverAddress, Consumer<Object> consumer) {
		super(myAddress, consumer);
		
		this.serverAddress = serverAddress; 
	}
	
	public void send(Object data) {
		
		super.send(serverAddress, data);
	}
	@Override
	public void stop() {
		Message messageToSend = new Message(myAddress, "", messageCounter++, true);
		try {
			messageConsumer.sendMessage(messageToSend, serverAddress, messenger);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		super.stop();
	}

	
	


}
