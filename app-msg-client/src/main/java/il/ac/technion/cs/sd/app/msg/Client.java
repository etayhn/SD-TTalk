package il.ac.technion.cs.sd.app.msg;

import il.ac.technion.cs.sd.lib.client.communication.ClientCommunicator;
import il.ac.technion.cs.sd.lib.serialization.StringConverter;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class Client implements IMessageHandler {
	/**
	 * The address of the client
	 */
	public final String myAddress;

	/**
	 * The address of the (only) server with which the client speaks
	 */
	public final String serverAddress;

	/**
	 * The ClientCommunicator with which the client and the server speak
	 */
	private ClientCommunicator communicator;

	/**
	 * A handler that needs to be run when a "regular" (common) instant message
	 * is sent
	 */
	private final Consumer<InstantMessage> messageConsumer;

	/**
	 * A function that defines how the client responds to friend requests from
	 * other clients.
	 */
	private final Function<String, Boolean> friendshipRequestHandler;

	/**
	 * a handler that accepts another client's response to a friend request and
	 * processes it.
	 */
	private final BiConsumer<String, Boolean> friendshipReplyConsumer;

	private BlockingQueue<List<IMessage>> unreadMessagesQueue;

	private BlockingQueue<OnlineCheckReplyMessage> isOnlineQueue;

	private BlockingQueue<LogoutReplyMessage> logoutQueue;

	private BlockingQueue<ReceivingApprovalMessage> successfullySentQueue;

	public Client(String myAddress, String serverAddress,
			Consumer<InstantMessage> messageConsumer,
			Function<String, Boolean> friendshipRequestHandler,
			BiConsumer<String, Boolean> friendshipReplyConsumer) {
		this.myAddress = myAddress;
		this.serverAddress = serverAddress;
		this.messageConsumer = messageConsumer;
		this.friendshipRequestHandler = friendshipRequestHandler;
		this.friendshipReplyConsumer = friendshipReplyConsumer;

		this.unreadMessagesQueue = new LinkedBlockingDeque<>();
		this.isOnlineQueue = new LinkedBlockingDeque<>();
		this.logoutQueue = new LinkedBlockingDeque<>();
		this.successfullySentQueue = new LinkedBlockingDeque<>();

		communicator = new ClientCommunicator(myAddress, serverAddress,
				new Consumer<Object>() {

					@Override
					public void accept(Object o) {
						((IMessage) o).handle(Client.this);
					}

				});

		getUnreadMessages();
	}

	private void getUnreadMessages() {
		// sending a request
		send(new LoginRequestMessage(myAddress));

		// waiting for a response with all the unread mail to come
		List<IMessage> unreadMessages;
		while (true) {
			try {
				unreadMessages = unreadMessagesQueue.take();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		// processing all the unread mail the server sent
		unreadMessages.forEach(m -> m.handle(this));
	}

	public void stop() {
		// sending a request
		send(new LogoutRequestMessage(myAddress));

		// waiting for a response
		while (true) {
			try {
				logoutQueue.take();
				return;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void send(IMessage message) {
		communicator.send(message);
	}

	public void sendAndWaitForServerToReceive(IMessage message) {
		communicator.send(message);
		while(true) {
			try {
				successfullySentQueue.take();
				break;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void handle(FriendReplyMessage message) {
		friendshipReplyConsumer.accept(message.from, message.answer);
	}

	@Override
	public void handle(FriendRequestMessage message) {
		send(new FriendReplyMessage(myAddress, message.from,
				friendshipRequestHandler.apply(message.from)));
	}

	@Override
	public void handle(OnlineCheckReplyMessage message) {
		try {
			isOnlineQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(LoginReplyMessage message) {
		try {
			unreadMessagesQueue.put(message.unsentMessages);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void handle(CommonInstantMessage message) {
		messageConsumer.accept(new InstantMessage(message.from, message.to,
				message.content));
	}

	public Optional<Boolean> askIfOnline(String who) {
		// sending a request
		send(new OnlineCheckRequestMessage(myAddress, who));

		// waiting for a response
		while (true) {
			try {
				return isOnlineQueue.take().answer;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void handle(LogoutReplyMessage message) {
		communicator.stop();
	}

	public void handle(ReceivingApprovalMessage message) {
		try {
			successfullySentQueue.put(message);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
