package il.ac.technion.cs.sd.lib.communication;

import static org.junit.Assert.*;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

import il.ac.technion.cs.sd.lib.client.communication.ClientCommunicator;
import il.ac.technion.cs.sd.lib.serialization.StringConverter;
import il.ac.technion.cs.sd.lib.server.communication.ServerCommunicator;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class LibraryTest {
	private ServerCommunicator serverCommunicator;
	private ClientCommunicator clientCommunicator;
	private ClientCommunicator clientCommunicator2;
	private String serverAddress;
	private String clientAddress;
	private String data;
	private String data2;
	private boolean flag1, flag2;
	
	private LinkedBlockingQueue<String> serverMessages = new LinkedBlockingQueue<String>();
	private LinkedBlockingQueue<String> clientMessages = new LinkedBlockingQueue<String>();
//	private Consumer<String> serverConsumer;
//	private Consumer<String> clientConsumer;

	@Rule
	public ExpectedException thrown = ExpectedException.none();
	private String client2Address;

	@Before
	public void setUp() {
		serverAddress = "server";
		clientAddress = "client";
		client2Address = "client2";
		
		serverCommunicator = new ServerCommunicator(serverAddress, (x) -> serverMessages.add(x));
		clientCommunicator = new ClientCommunicator(clientAddress, serverAddress,
				(x) -> clientMessages.add(x));
		clientCommunicator2 = new ClientCommunicator(client2Address, serverAddress,
				(x) -> clientMessages.add(x));

		data = "abcd";
		data2 = "aaaa";
		flag1 = false;
		flag2 = false;
	}

	@After
	public void teardown() {
		if (!serverCommunicator.isCommunicatorClosed())
			serverCommunicator.stop();
		if (!clientCommunicator.isCommunicatorClosed())
			clientCommunicator.stop();
		
		if (!clientCommunicator2.isCommunicatorClosed())
			clientCommunicator2.stop();

	}

	@Test
	public void testServerShouldReceiveAfterClientSend() throws InterruptedException {
		for(int i=0; i< 100 ; i++){
			clientCommunicator.send(data);
			String dataReceived = (String) StringConverter.convertFromString(serverMessages.take());
			assertEquals(data, dataReceived);
		}
	}


	@Test
	public void testClientShouldReceiveAfterServerSend() throws InterruptedException {
		serverCommunicator.send(clientAddress, data);
		
		String dataReceived = (String) StringConverter.convertFromString(clientMessages.take());
		assertEquals(data, dataReceived);
	}

	@Test
	public void testClosingCommunication() {
		assertFalse(serverCommunicator.isCommunicatorClosed());
		assertFalse(clientCommunicator.isCommunicatorClosed());

		serverCommunicator.stop();
		clientCommunicator.stop();

		assertTrue(serverCommunicator.isCommunicatorClosed());
		assertTrue(clientCommunicator.isCommunicatorClosed());
	}
	
	@Test
	public void testServerShouldReceiveFromTwoClients() throws InterruptedException {
		clientCommunicator.send(data);
		clientCommunicator2.send(data2);
		
		String dataReceived = (String) StringConverter.convertFromString(serverMessages.take());
		assertEquals(data, dataReceived);
		
		dataReceived = (String) StringConverter.convertFromString(serverMessages.take());
		assertEquals(data2, dataReceived);

	}
	
	@Test
	public void testServerSendDuringReceiveIsReceivedInClient() throws InterruptedException{

		serverCommunicator.stop();
		serverCommunicator = new ServerCommunicator(serverAddress, (x) -> 
							{serverMessages.add(x);
							serverCommunicator.send(clientAddress, data);
							});

		int numMessages = 5;
		for(int i=0; i< numMessages ; i++){
			clientCommunicator.send(serverAddress, data);
		}
		
		for(int i=0; i< numMessages ; i++){
			String dataReceivedInServer = (String) StringConverter.convertFromString(serverMessages.take());
			assertEquals(data, dataReceivedInServer);
		}
		
		for(int i=0; i< numMessages ; i++){

			String dataReceivedInClient = (String) StringConverter.convertFromString(clientMessages.take());
			assertEquals(data, dataReceivedInClient);
		}

	}
	


}

