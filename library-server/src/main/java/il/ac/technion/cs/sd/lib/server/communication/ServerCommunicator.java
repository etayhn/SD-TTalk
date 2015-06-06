package il.ac.technion.cs.sd.lib.server.communication;


import java.util.function.Consumer;

import il.ac.technion.cs.sd.lib.communication.Communicator;


public class ServerCommunicator extends Communicator{

	public ServerCommunicator(String myAddress, Consumer<Object> consumer) {
		super(myAddress, consumer);
	}
	
	public void send(String to, Object data) {
		
		super.send(to, data);
	}


}
