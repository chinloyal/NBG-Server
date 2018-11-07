package connection;

import java.io.IOException; 
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

import communication.Request;
import communication.Response;
import interfaces.Connection;
import models.User;
import database.UserProvider;

public class Server implements Connection<Response> {
	private ServerSocket serverSocket;
	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private Logger logger = LogManager.getLogger(Server.class);

	public Server() {
		start();
	}

	public void getStreams() throws IOException {
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
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
				socket = serverSocket.accept();
				getStreams();
				Request request = null;

				do {
					try {
						request = (Request) ois.readObject();

						if (request.getAction().equals("register_user")) { // REGISTER USER
							UserProvider provider = new UserProvider();

							if (provider.store((User) request.getData()) > 0) {
								send(new Response(true, "Customer has been registered"));
							} else {
								send(new Response(false));
							}
						} 
						else if(request.getAction().equals("login")){
							String credentials[] = (String[]) request.getData();
							
							UserProvider provider = new UserProvider();
							
							boolean success = provider.authenticate(credentials[0], credentials[1]);
							
							if (success) { // If login is successful, send success, the customer object, and a message
								User customer = provider.getByEmail(credentials[0]);
								send(new Response(success,customer,"This customer successfully logged in!"));
							}else { // If login was NOT successful, send success only (false)
								send(new Response(success));
							}
						}
						else if(request.getAction().equals("get_customer_photo")) {
							User customer = (User)request.getData();
							
							UserProvider cusInfo = new UserProvider();
							String photo = cusInfo.getCusPhoto(customer);
							
							if (photo!=null) {
								send(new Response(true, photo, "Customer Photo Retrieved"));
							}else {
								send(new Response(false, "Customer Photo NOT Retrieved"));
							}
							
						}
					} catch (ClassNotFoundException e) {
						logger.error("Cannot locate class.");
					} catch (ClassCastException e) {
						logger.error("Could not cast class.");
					}
				} while (!request.getAction().equals("EXIT"));
				closeConnection();
			}
		} catch (IOException e) {
			logger.error("Could not accept connection");
		} catch (NullPointerException e) {
			logger.error("The request is empty (null).");
		}
	}

	public void closeConnection() {
		try {
			if (ois != null)
				ois.close();
			if (oos != null)
				oos.close();
			if (socket != null)
				socket.close();
			logger.info("Server closed connection.");
		} catch (NullPointerException | IOException e) {
			logger.error("Could not close all connections");
		}
	}

	public void stop() {
		try {
			serverSocket.close();
		} catch (IOException e) {
			logger.error("Could not close server socket.");
		}
	}

	@Override
	public void send(Response data) throws IOException {
		oos.writeObject(data);
	}
}
