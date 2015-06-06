package il.ac.technion.cs.sd.app.msg;

import java.util.List;

public class LoginReplyMessage implements IMessage {

	public List<IMessage> unsentMessages; 
	
	public LoginReplyMessage(List<IMessage> unsentMessages) {
		this.unsentMessages = unsentMessages;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
