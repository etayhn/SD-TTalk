package il.ac.technion.cs.sd.app.msg;

public class OnlineCheckRequestMessage implements IMessage {

	public final String whoIsChecking;
	
	public final String whoIsBeingChecked;
	
	public OnlineCheckRequestMessage(String whoIsChecking, String whoIsChecked) {
		this.whoIsChecking = whoIsChecking;
		this.whoIsBeingChecked = whoIsChecked;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
