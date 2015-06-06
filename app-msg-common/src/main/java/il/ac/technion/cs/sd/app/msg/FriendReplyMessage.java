package il.ac.technion.cs.sd.app.msg;

public class FriendReplyMessage implements IMessage {
	
	/**
	 * response sender, i.e. request recipient
	 */
	public final String from;

	/**
	 * response receiver, i.e. request sender
	 */
	public final String to;
	
	/**
	 * the response
	 */
	public final boolean answer;
	
	public FriendReplyMessage(String from, String to, boolean answer) {
		this.from = from;
		this.to = to;
		this.answer = answer;
	}
	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
