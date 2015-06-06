package il.ac.technion.cs.sd.app.msg;

import java.util.Optional;

public class OnlineCheckReplyMessage implements IMessage {

	public final String whoIsBeingChecked;
	public final Optional<Boolean> answer;

	public OnlineCheckReplyMessage(String whoIsBeingChecked,
			Optional<Boolean> answer) {
		this.whoIsBeingChecked = whoIsBeingChecked;
		this.answer = answer;
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
