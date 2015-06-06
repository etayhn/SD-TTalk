package il.ac.technion.cs.sd.app.msg;

public class LogoutRequestMessage implements IMessage {

	public final String myAddress;

	public LogoutRequestMessage(String myAddress) {
		this.myAddress = myAddress;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
