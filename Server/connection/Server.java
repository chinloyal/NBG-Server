package connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import jobs.HandleRequests;

public class Server {
	private ServerSocket serverSocket;
	private static Logger logger = LogManager.getLogger(Server.class);
	
	public Server() {
		start();
	}

	private void start() {
		try {
			serverSocket = new ServerSocket(9000, 1);
			logger.info("Server is starting...");
		} catch (IOException e) {
			logger.error("Could not initialize server socket", e.getMessage());
		}
	}

	public void waitForRequests() {
		if (serverSocket == null) {
			logger.warn("The server has not been initialized");
			return;
		}

		try {
			while (true) {
				logger.info("Waiting for requests...");
				Socket socket = serverSocket.accept();
				Thread job = new Thread(new HandleRequests(socket));
				
				job.start();
			}
		}catch(IOException e){
			logger.warn("Could not accept connection");
		}catch(NullPointerException e) {
			logger.error("The request is empty (null).");
		}
	}
	
	public void stop() {
		try {
			serverSocket.close();
			logger.info("Server has been stopped.");
		}catch(IOException e) {
			logger.error("Could not close server socket.");
		}
	}
}
