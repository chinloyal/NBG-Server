package connection;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import communication.Response;
import interfaces.Connection;
import jobs.HandleRequests;


public class Server implements Connection<Response> {
	private ServerSocket serverSocket;
	protected static Socket socket;
	protected static ObjectOutputStream oos;
	protected static ObjectInputStream ois;
	
	protected Logger logger  = LogManager.getLogger(Server.class);
	
	public Server() {
		start();
	}
	
	public void getStreams() throws IOException{
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
	}
	
	private void start(){
		try{
			serverSocket = new ServerSocket(9000, 1);
			logger.info("Server is starting...");
		}catch(IOException e){
			logger.error("Could not initialize server socket", e.getMessage());
		}
	}
	
	public void waitForRequests(){
		if(serverSocket == null){
			logger.warn("The server has not been initialized");
			return;
		}
		
		try{
			while(true){
				logger.info("Waiting for requests...");
				socket = serverSocket.accept();
				
				Thread job = new Thread(new HandleRequests());
				
				job.start();
			}
		}catch(IOException e){
			logger.warn("Could not accept connection");
		}catch(NullPointerException e) {
			logger.error("The request is empty (null).");
		}
	}
	
	public void closeConnection(){
		try{
			if(ois != null)
				ois.close();
			if(oos != null)
				oos.close();
			if(socket != null)
				socket.close();
			logger.info("Server closed connection.");
		}catch(NullPointerException | IOException e){
			logger.error("Could not close all connections");
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

	public void send(Response data) throws IOException {
		oos.writeObject(data);
	}
}
