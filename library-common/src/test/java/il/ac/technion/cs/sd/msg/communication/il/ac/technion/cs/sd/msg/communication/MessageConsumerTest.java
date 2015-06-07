package il.ac.technion.cs.sd.msg.communication;

import static org.junit.Assert.*;
import org.junit.*;

import java.util.concurrent.LinkedBlockingQueue;

import il.ac.technion.cs.sd.lib.communication.Message;
import il.ac.technion.cs.sd.lib.communication.MessageConsumer;
import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.msg.Messenger;
import il.ac.technion.cs.sd.msg.MessengerException;
import il.ac.technion.cs.sd.msg.MessengerFactory;


public class MessageConsumerTest {

	private Messenger messenger;
	
	private String address;
	
	private MessageConsumer messageConsumer;
	
	private final LinkedBlockingQueue<String> messageQueue = new LinkedBlockingQueue<String>();
	
	private String data;

	private Message message;
	private String messageAsString;
	
	@Before
	public void setUp() throws MessengerException{
		
		address = "Avner";
		messageConsumer = new MessageConsumer(x->messageQueue.add((String) x), address);
		messenger = new MessengerFactory().start(address, messageConsumer);
		data = "Itay";
		message = new Message(address, StringConverter.convertToString(data), 0);
		messageAsString = StringConverter.convertToString(message);
	}
	
	@After
	public void finish() throws MessengerException{
		messenger.kill();
	}
	
	@Test(expected = RuntimeException.class)
	public void acceptShouldThrowAnExceptionOnNullMessenger() throws Exception {
		messageConsumer.accept(null, messageAsString);
	}
	
	@Test(expected = RuntimeException.class)
	public void acceptShouldThrowAnExceptionOnNullMessage() throws Exception {
		messageConsumer.accept(messenger, null);
	}
	
	@Test(expected = RuntimeException.class)
	public void sendMessageShouldThrowAnExceptionOnNullMessenger() throws Exception {
		messageConsumer.sendMessage(message, address, null);
	}
	
	@Test(expected = RuntimeException.class)
	public void sendMessageShouldThrowAnExceptionOnNullMessage() throws Exception {
		messageConsumer.sendMessage(null, address, messenger);
	}
	
	@Test(expected = RuntimeException.class)
	public void sendMessageShouldThrowAnExceptionOnNullAddress() throws Exception {
		messageConsumer.sendMessage(message, null, messenger);
	}

	@Test
	public void consumerReceivesMessageCorrectly() throws MessengerException, InterruptedException {
		messageConsumer.accept(messenger, messageAsString);
		assertEquals(data, messageQueue.take());
	}
	
	@Test
	public void consumerSendsMessageCorrectly() throws MessengerException, InterruptedException {
		messageConsumer.sendMessage(message, address, messenger);
		assertEquals(data, messageQueue.take());
	}

}


