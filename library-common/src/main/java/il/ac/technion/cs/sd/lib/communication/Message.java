package il.ac.technion.cs.sd.lib.communication;

/**
 * This class represents a message being sent between 2 communicators.
 * 
 * @author avner
 *
 */
public class Message {

	/**
	 * address of the source
	 */
	public final String from;

	/**
	 * data to be sent.
	 */
	public final String data;

	/**
	 * indicates whether the communicator that sent it was stopped.
	 */
	private boolean stoppedMessage;

	/**
	 * counter of the message.
	 */
	public int counter;

	/**
	 * 
	 * @return whether the communicator that sent it was stopped.
	 */
	public boolean isStoppedMessage() {
		return stoppedMessage;
	}

	/**
	 * Initialize a new message.
	 * 
	 * @param from
	 *            address of the source.
	 * @param data
	 *            data to be sent.
	 * @param counter
	 *            counter of the message
	 */
	public Message(String from, String data, int counter) {
		this.from = from;
		this.data = data;
		this.counter = counter;
		this.stoppedMessage = false;
	}

	/**
	 * Initialize a new message.
	 * 
	 * @param from
	 *            address of the source.
	 * @param data
	 *            data to be sent.
	 * @param counter
	 *            counter of the message
	 * @param stopped
	 *            indicates whether the communicator that sent it was stopped.
	 */
	public Message(String from, String data, int counter, boolean stopped) {
			this(from, data, counter);		
			this.stoppedMessage = stopped;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		Message other = (Message) obj;
		if (!other.data.equals(data)) {
			return false;
		}
		if (!other.from.equals(from)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[from: " + from + ", data: " + data + ", counter: " + counter
				+ "]";
	}

}
