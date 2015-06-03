package il.ac.technion.cs.sd.app.msg;

public class LoginRequestMessage implements IMessage {

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
