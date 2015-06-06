package il.ac.technion.cs.sd.lib.communication;

import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.msg.Messenger;
import il.ac.technion.cs.sd.msg.MessengerException;
import il.ac.technion.cs.sd.msg.MessengerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public abstract class Communicator {
	/**
	 * The messenger object with which we send/receive the data
	 */
	protected Messenger messenger;

	protected String myAddress;

	protected boolean isCommunicatorClosed;
	
	protected MessageConsumer messageConsumer;
	
	protected int messageCounter;

	public Communicator(String myAddress, Consumer<Object> consumer) {
		if (myAddress == null)
			throw new IllegalArgumentException("myAddress cannot be null");

		this.myAddress = myAddress;
		isCommunicatorClosed = false;
		
		messageConsumer = new MessageConsumer(consumer, myAddress);
		try {
			messenger = new MessengerFactory().start(myAddress, messageConsumer);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}

	public boolean isCommunicatorClosed() {
		return isCommunicatorClosed;
	}

	/**
	 * Stops the communicator. Any function invocation after this call will
	 * fail.
	 */
	public void stop() {
		checkLiveness();

		try {
			messenger.kill();
			isCommunicatorClosed = true;
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		}
	}

	private void checkLiveness() {
		if (isCommunicatorClosed) {
			throw new RuntimeException(
					"Communicator is dead. Cannot activate its methods.");
		}
	}

	/**
	 * Sends (in a blocking manner) an object to a given address
	 * 
	 * @param to
	 *            the address of the recipient
	 * @param data
	 *            the data to send
	 * @throws IOException
	 * @throws MessengerException
	 */
	protected void send(String to, Object data) {
		checkLiveness();
		if (to == null)
			throw new IllegalArgumentException("addressee cannot be null");
		if (data == null)
			throw new IllegalArgumentException("data cannot be null");

		String dataAsString = StringConverter.convertToString(data);
		Message messageToSend = new Message(myAddress, dataAsString, messageCounter++);
		try {
			messageConsumer.sendMessage(messageToSend, to, messenger);
		} catch (MessengerException e) {
			throw new RuntimeException("Messenger Exception: " + e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

}
