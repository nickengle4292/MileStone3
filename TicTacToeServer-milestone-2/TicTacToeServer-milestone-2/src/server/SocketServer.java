package server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main class for TicTacToe Server that sets up the socket server
 *
 * @author Ahmad Suleiman
 */
public class SocketServer {
	/**
	 * Used for printing server logs of different levels
	 */
	private final Logger LOGGER;

	/*
    The socket server's port number
     */
	private final int PORT;

	/**
	 * ServerSocket instance
	 */
	private ServerSocket serverSocket;

	/**
	 * The main function of the application
	 * It instantiates the class, sets up the server, and starts accepting client's request
	 *
	 * @param args command line arguments
	 */
	public static void main(String[] args) {
		try {
			SocketServer socketServer = new SocketServer();
			socketServer.setup();
			socketServer.startAcceptingRequest();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/**
	 * Default constructor with default port = 5000
	 *
	 * @throws Exception when an invalid port is provided
	 */
	public SocketServer() throws Exception {
		this(5000);
	}

	/**
	 * Constructor that sets the {@link #PORT} attribute
	 *
	 * @param port The socket server's port number
	 * @throws Exception when an invalid port is provided
	 */
	public SocketServer(int port) throws Exception {
		if (port < 0) {
			throw new Exception("Port number cannot be negative");
		}
		PORT = port;
		LOGGER = Logger.getLogger(SocketServer.class.getName());
	}

	/**
	 * Sets up the socket server
	 */
	private void setup() {
		try {
			serverSocket = new ServerSocket(PORT);
			LOGGER.log(Level.INFO, "Server Initialization Succeeded"
					+ "\nServer Host Name: " + InetAddress.getLocalHost().getHostName()
					+ "\nServer IP: " + InetAddress.getLocalHost().getHostAddress()
					+ "\nServer Port Number: " + PORT);
		} catch (UnknownHostException e) {
			LOGGER.log(Level.SEVERE, "Server Error: Unable to Resolve Host", e);
			System.exit(1);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Server Error: Server Initialization Failed", e);
			System.exit(1);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Server Error: Unknown Exception Occurred", e);
			System.exit(1);
		}
	}

	/**
	 * Start accepting client's request
	 */
	/**
	 * Start accepting client's request
	 */
	private void startAcceptingRequest() {
		try {
			while (true) {
				// Accept socket connection from a player and create a new handler to handle the connection
				Socket clientSocket = serverSocket.accept();
				LOGGER.log(Level.INFO, "New Socket Client Connect with IP: " + clientSocket.getRemoteSocketAddress());

				// Remove the 'username' parameter when initializing ServerHandler
				ServerHandler serverHandler = new ServerHandler(clientSocket);
				serverHandler.start();
			}
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Server Error: Client Connection Failed", e);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Server Error: Unknown Exception Occurred", e);
		}
	}


	/**
	 * Generate a unique username for each client.
	 * You should implement this method based on your requirements.
	 *
	 * @return A unique username
	 */
	private String generateUniqueUsername() {
		// Implement logic to generate a unique username for each client
		// This is just a placeholder, replace it with your actual logic
		return "user" + System.currentTimeMillis();
	}

	/**
	 * Getter for PORT attribute
	 *
	 * @return PORT
	 */
	public int getPORT() {
		return PORT;
	}
}
