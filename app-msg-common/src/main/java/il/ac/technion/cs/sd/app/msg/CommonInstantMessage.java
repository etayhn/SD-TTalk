package il.ac.technion.cs.sd.app.msg;

public class CommonInstantMessage extends InstantMessage implements IMessage {

	public CommonInstantMessage(InstantMessage im) {
		super(im.from, im.to, im.content);
	}
	
	public CommonInstantMessage(String from, String to, String content) {
		super(from, to, content);
	}

	@Override
	public void handle(IMessageHandler messageHandler) {
		messageHandler.handle(this);
	}

}
