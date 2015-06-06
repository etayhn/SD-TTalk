package il.ac.technion.cs.sd.app.msg;

import il.ac.technion.cs.sd.lib.serialization.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/**
 * The server side of the TMail application. <br>
 * This class is mainly used in our tests to start, stop, and clean the server
 */
public class ServerMailApplication {
	
	/**
	 * The server instance
	 */
	private Server server;
	
	/**
	 * The server name
	 */
	private final String serverName;
	
	/**
	 * The name of the file that holds the backup of the server (if it exists)
	 */
	private final String backupFilePath;
	
    /**
     * Starts a new mail server. Servers with the same name retain all their information until
     * {@link ServerMailApplication#clean()} is called.
     *
     * @param name The name of the server by which it is known.
     */

	public ServerMailApplication(String string) {
		serverName = string;
		backupFilePath = "backup_" + serverName + ".txt";
	}
	
	/**
	 * @return the server's address; this address will be used by clients connecting to the server
	 */
	public String getAddress() {
		return serverName;
	}
	
	/**
	 * Starts the server; any previously sent mails, data and indices are loaded.
	 * This should be a <b>non-blocking</b> call.
	 */
	public void start() {
		try {
			server = (Server) FileHandler.readFromFile(backupFilePath);
		} catch (Exception e) {
			// could not retrieve stored data, starting a fresh new server
			server = new Server(serverName);
		}
	}
	
	/**
	 * Stops the server. A stopped server can't accept messages, but doesn't delete any data (messages that weren't received).
	 */
	public void stop() {
		try {
			FileHandler.writeToFile(server, backupFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		server.stop();
	}
	
	/**
	 * Deletes <b>all</b> previously saved data. This method will be used between tests to assure that each test will
	 * run on a new, clean server. you may assume the server is stopped before this method is called.
	 */
	public void clean() {
		try {
			Files.deleteIfExists(Paths.get(backupFilePath));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
