package il.ac.technion.cs.sd.app.msg;

public class FriendRequestMessage implements IMessage {

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
