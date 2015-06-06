package il.ac.technion.cs.sd.app.msg;

public class FriendRequestMessage implements IMessage {
	/**
	 * request sender
	 */
	public final String from;

	/**
	 * request recipient
	 */
	public final String to;
	
	public FriendRequestMessage(String from, String to) {
		this.from = from;
		this.to = to;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
