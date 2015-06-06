package il.ac.technion.cs.sd.app.msg;

import java.util.List;

public class LogoutReplyMessage implements IMessage {

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
