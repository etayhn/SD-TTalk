package il.ac.technion.cs.sd.app.msg;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class ClientMessageHandler implements IMessageHandler {
	private Consumer<InstantMessage> messageConsumer;
	private Function<String, Boolean> friendshipRequestHandler;
	private BiConsumer<String, Boolean> friendshipReplyConsumer;
	
	public ClientMessageHandler(Consumer<InstantMessage> messageConsumer,
			Function<String, Boolean> friendshipRequestHandler,
			BiConsumer<String, Boolean> friendshipReplyConsumer) {
		this.messageConsumer = messageConsumer;
		this.friendshipRequestHandler = friendshipRequestHandler;
		this.friendshipReplyConsumer = friendshipReplyConsumer;
	}

	
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
	public void handle(OnlineCheckReplyMessage message) {
		// TODO Auto-generated method stub

	}

}
