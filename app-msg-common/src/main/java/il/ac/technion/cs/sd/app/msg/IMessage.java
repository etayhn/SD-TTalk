package il.ac.technion.cs.sd.app.msg;

public interface IMessage {

	public void handle(IMessageHandler messageHandler);
	
}
