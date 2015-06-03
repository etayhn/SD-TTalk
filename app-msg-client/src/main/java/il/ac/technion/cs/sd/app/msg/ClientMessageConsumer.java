package il.ac.technion.cs.sd.app.msg;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;

import java.util.function.Consumer;

public class ClientMessageConsumer implements Consumer<String> {

	private IMessageHandler messageHandler;

	public ClientMessageConsumer(IMessageHandler messageHandler) {
		this.messageHandler = messageHandler;
	}
	

	@Override
	public void accept(String t) {
		
		IMessage message = (IMessage) StringConverter.convertFromString(t);
		
		message.handle(messageHandler);
		
	}

}
