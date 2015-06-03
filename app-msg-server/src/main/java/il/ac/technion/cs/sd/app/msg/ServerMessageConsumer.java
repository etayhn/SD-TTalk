package il.ac.technion.cs.sd.app.msg;

import il.ac.technion.cs.sd.lib.communication.Communicator;
import il.ac.technion.cs.sd.lib.server.communication.ServerCommunicator;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ServerMessageConsumer implements Consumer<String> {

	private IMessageHandler messageHandler;

	public ServerMessageConsumer(IMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	

	@Override
	public void accept(String t) {
		
	}

}
