package il.ac.technion.cs.sd.app.msg;

public class LoginRequestMessage implements IMessage {

	public final String myAddress;

	public LoginRequestMessage(String myAddress) {
		this.myAddress = myAddress;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
