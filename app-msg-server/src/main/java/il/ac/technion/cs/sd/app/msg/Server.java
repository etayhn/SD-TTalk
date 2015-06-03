package il.ac.technion.cs.sd.app.msg;

import java.util.Map;

public class Server implements IMessageHandler {
	
	private Map<String, ClientData> clients;
	
	@Override
	public void handle(FriendReplyMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(FriendRequestMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(LoginReplyMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(LoginRequestMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(OnlineCheckRequestMessage message) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handle(OnlineCheckReplyMessage message) {
		// TODO Auto-generated method stub

	}

}
