package il.ac.technion.cs.sd.lib.server.communication;

import java.util.function.Consumer;

import il.ac.technion.cs.sd.lib.communication.Communicator;

public class ServerCommunicator extends Communicator {

	/**
	 * builds a new communicator. It will start receiving new messages.
	 * 
	 * @param myAddress
	 *            address of current communicator.
	 * @param consumer
	 *            consumer for messages that will be received by this
	 *            communicator.
	 */
	public ServerCommunicator(String myAddress, Consumer<Object> consumer) {
		super(myAddress, consumer);
	}

	@Override
	public void send(String to, Object data) {

		super.send(to, data);
	}

}
