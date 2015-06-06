package il.ac.technion.cs.sd.app.msg;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.lib.server.communication.ServerCommunicator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class Server implements IMessageHandler {
	/**
	 * Stores all of the data about the clients in a clientName->clientData map
	 */
	private Map<String, ClientData> clients;

	/**
	 * The ServerCommunicator with which the server speaks with the clients
	 */
	private ServerCommunicator communicator;

	/**
	 * the server's address
	 */
	public final String myAddress;

	public Server(String myAddress) {
		this.myAddress = myAddress;
		clients = new HashMap<>();

		communicator = new ServerCommunicator(myAddress,
				new Consumer<Object>() {

					@Override
					public void accept(Object o) {
						((IMessage) o).handle(Server.this);
					}

				});
	}

	public void stop() {
		communicator.stop();
	}

	/*
	 * TODO? Need to prevent a situation in which I check and the client is
	 * online, but then another thread logs him out before I finished sending
	 */
	public void send(String to, IMessage message) {
		if (!clients.containsKey(to)) {
			// there is no such client, so we create one
			clients.put(to, new ClientData());
		}

		// should always succeed
		ClientData clientData = clients.get(to);

		if (clientData.isOnline()) {
			communicator.send(to, message);
		} else {
			clientData.addMessageToUnsentQueue(message);
		}
	}

	@Override
	public void handle(FriendReplyMessage message) {
		if (message.answer == false)
			return;

		// positive answer! new friend!
		clients.get(message.from).addFriend(message.to);
		clients.get(message.to).addFriend(message.from);

		send(message.to, message);
	}

	@Override
	public void handle(FriendRequestMessage message) {
		send(message.to, message);
	}

	@Override
	public void handle(LoginRequestMessage message) {
		ClientData clientData = clients.get(message.myAddress);
		if (clientData == null) {
			clientData = new ClientData();
			clients.put(message.myAddress, clientData);
		}
		clientData.setOnline(true);
		send(message.myAddress,
				new LoginReplyMessage(clientData.getUnsentMessages()));
	}

	@Override
	public void handle(OnlineCheckRequestMessage message) {
		ClientData clientData = clients.get(message.whoIsChecking);
		Optional<Boolean> response = null;
		if (!clientData.isFriendsWith(message.whoIsBeingChecked)) {
			response = Optional.empty();
		} else {
			response = Optional.of(clients.get(message.whoIsBeingChecked)
					.isOnline());
		}
		send(message.whoIsChecking, new OnlineCheckReplyMessage(
				message.whoIsBeingChecked, response));
	}

	@Override
	public void handle(CommonInstantMessage message) {
		send(message.to, message);
	}

	public void handle(LogoutRequestMessage message) {
		send(message.myAddress, new LogoutReplyMessage());
		clients.get(message.myAddress).setOnline(false);
	}
	
}
