package il.ac.technion.cs.sd.app.msg;

import java.util.ArrayList;
import java.util.List;

/**
 * A class that saves a client's data on the server.
 */
public class ClientData {

	/**
	 * the list of friends of the client
	 */
	private List<String> friends;

	/**
	 * The messages that were yet to be sent to the client (since he is offline)
	 */
	private List<IMessage> unsentMessages;

	/**
	 * true iff the client is online, i.e. logged to the server
	 */
	private boolean isOnline;

	public ClientData() {
		friends = new ArrayList<>();
		unsentMessages = new ArrayList<>();
	}

	public boolean isFriendsWith(String name) {
		return friends.contains(name);
	}

	public void addMessageToUnsentQueue(IMessage message) {
		unsentMessages.add(message);
	}

	public boolean isOnline() {
		return isOnline;
	}

	public void setOnline(boolean online) {
		isOnline = online;
	}

	public void addFriend(String friend) {
		if (!friends.contains(friend))
			friends.add(friend);
	}

	public List<IMessage> getUnsentMessages() {
		List<IMessage> tmp = unsentMessages;
		unsentMessages = new ArrayList<>();
		return tmp;
	}

}
