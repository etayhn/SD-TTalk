package il.ac.technion.cs.sd.lib.client.communication;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import il.ac.technion.cs.sd.lib.communication.Communicator;


public class ClientCommunicator extends Communicator{

	String serverAddress;
	public ClientCommunicator(String myAddress, String serverAddress, Consumer<String> consumer) {
		super(myAddress, consumer);
		
		this.serverAddress = serverAddress; 
	}
	
	public void send(Object data) {
		
		super.send(serverAddress, data);
	}


}
