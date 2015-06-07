package il.ac.technion.cs.sd.lib.communication;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import il.ac.technion.cs.sd.lib.client.communication.ClientCommunicator;
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
	private String client2Address;
	private String data;
	private String data2;
	
	private LinkedBlockingQueue<String> serverMessages = new LinkedBlockingQueue<String>();
	private LinkedBlockingQueue<String> clientMessages = new LinkedBlockingQueue<String>();

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Before
	public void setUp() {
		
		System.out.println("-------------------------------------");
		serverAddress = "server";
		clientAddress = "client";
		client2Address = "client2";
		
		serverCommunicator = initServerCommunicator();
		clientCommunicator = initClientCommunicator(clientAddress);
		clientCommunicator2 = initClientCommunicator(client2Address);

		data = "abcd";
		data2 = "aaaa";
	}

	private ClientCommunicator initClientCommunicator(String clientAddress) {
		return new ClientCommunicator(clientAddress, serverAddress,
				(x) -> clientMessages.add((String)x));
	}
	
	private ServerCommunicator initServerCommunicator() {
		return new ServerCommunicator(serverAddress,
				(x) -> serverMessages.add((String)x));
	}

	@After
	public void teardown() {
		if (!serverCommunicator.isCommunicatorStopped())
			serverCommunicator.stop();

		if (!clientCommunicator.isCommunicatorStopped())
			clientCommunicator.stop();
		
		if (!clientCommunicator2.isCommunicatorStopped())
			clientCommunicator2.stop();
	}

	@Test
	public void serverShouldReceiveSimpleDataAfterClientSend() throws InterruptedException {
		for(int i=0; i< 100 ; i++){
			clientCommunicator.send(data);
			assertEquals(data, serverMessages.take());
		}
	}

	@SuppressWarnings("unchecked")
	@Test
	public void serverShouldReceiveComplexDataAfterClientSend() throws InterruptedException {
		LinkedBlockingQueue<Map<String,String>> serverComplexMessages = new LinkedBlockingQueue<Map<String,String>>();
		Map<String,String> complexData = new ConcurrentHashMap<String, String>();
		complexData.put("Avner", "Avner");
		complexData.put("Itay", "Itay");

		serverCommunicator.stop();
		serverCommunicator = new ServerCommunicator(serverAddress,
				(x) -> serverComplexMessages.add((Map<String,String>)x));
		
		for(int i=0; i< 5 ; i++){
			clientCommunicator.send(complexData);
			assertEquals(complexData, serverComplexMessages.take());
		}
	}


	@Test
	public void clientShouldReceiveAfterServerSend() throws InterruptedException {
		serverCommunicator.send(clientAddress, data);
		assertEquals(data, clientMessages.take());
	}

	@Test
	public void communicatorShouldBeStoppedAfterStop() {
		assertFalse(serverCommunicator.isCommunicatorStopped());
		assertFalse(clientCommunicator.isCommunicatorStopped());

		serverCommunicator.stop();
		clientCommunicator.stop();

		assertTrue(serverCommunicator.isCommunicatorStopped());
		assertTrue(clientCommunicator.isCommunicatorStopped());
		
		clientCommunicator = initClientCommunicator(clientAddress);
		serverCommunicator = initServerCommunicator();
	}
	
	@Test
	public void serverShouldReceiveFromTwoClients() throws InterruptedException {
		clientCommunicator.send(data);
		clientCommunicator2.send(data);
		
		assertEquals(data, serverMessages.take());
		assertEquals(data, serverMessages.take());
	}
	
	@Test
	public void serverCanSendWhileReceivingFromClient() throws InterruptedException{

		serverCommunicator.stop();
		serverCommunicator = new ServerCommunicator(serverAddress, (x) -> 
							{serverMessages.add((String)x);
							serverCommunicator.send(clientAddress, data2);
							});

		int numMessages = 100;
		for(int i=0; i< numMessages ; i++){
			clientCommunicator.send(serverAddress, data);
			assertEquals(data, serverMessages.take());
			assertEquals(data2, clientMessages.take());
		}
		
	}
	
	@Test
	public void serverMiddleManBetweenClients() throws InterruptedException{
		
		serverCommunicator.stop();
		serverCommunicator = new ServerCommunicator(serverAddress, (x) -> 
							{serverMessages.add((String)x);
							if(x.equals(clientAddress)){
								serverCommunicator.send(client2Address, data);
							}else{
								serverCommunicator.send(clientAddress, data2);
							}
							});
		clientCommunicator2.stop();
		clientCommunicator2= new ClientCommunicator(client2Address, serverAddress, (x)-> {
									clientMessages.add((String)x);
									clientCommunicator2.send(serverAddress, client2Address);
									});
		clientCommunicator.send(serverAddress, clientAddress);
		
		assertEquals(clientAddress, serverMessages.take());
		assertEquals(client2Address, serverMessages.take());
		
		assertEquals(data, clientMessages.take());
		assertEquals(data2, clientMessages.take());

	}
	
	@Test
	public void clientCanSendAfterStop() throws InterruptedException{

		clientCommunicator.send(data);
		clientCommunicator.stop();
		clientCommunicator = initClientCommunicator(clientAddress);
		clientCommunicator.send(data2);
		
		assertEquals(data, serverMessages.take());
		assertEquals(data2, serverMessages.take());
		
	}
	
	@Test
	public void serverCanReceiveAfterStop() throws InterruptedException{

		clientCommunicator.send(data);
		serverCommunicator.stop();
		serverCommunicator = initServerCommunicator();
		clientCommunicator.send(data2);
		
		assertEquals(data, serverMessages.take());
		assertEquals(data2, serverMessages.take());
	}
	
	@Test
	public void serverCanSendMsgToSelf() throws InterruptedException{
		
		serverCommunicator.send(serverAddress, data);
		assertEquals(data, serverMessages.take());
	}
}

