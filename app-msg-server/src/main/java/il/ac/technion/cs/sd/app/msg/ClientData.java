package il.ac.technion.cs.sd.app.msg;

import java.util.List;

public class ClientData {

	private List<String> friends;
	
	/**
	 * The messages that were yet to be sent to the client (since he is offline)
	 */
	private List<IMessage> unsentMessages; 
	
	private boolean isOnline;
	
}
