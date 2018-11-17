package jobs;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.swing.JOptionPane;

import communication.Request;
import communication.Response;
import database.ManagerProvider;
import database.TransactionProvider;
import database.UserProvider;
import interfaces.Connection;
import models.Transaction;
import models.User;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HandleRequests implements Runnable, Connection<Response> {

	private Socket socket;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;

	private static Logger logger = LogManager.getLogger(HandleRequests.class);

	public HandleRequests(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		try {
			getStreams();
			Request request = null;

			do {
				try {
					request = read();

					switch (request.getAction()) {
					case "register_user":
						UserProvider provider = new UserProvider();

						if (provider.store((User) request.getData()) > 0) {
							send(new Response(true, "Customer has been registered"));
						} else {
							send(new Response(false));
						}
						break;
					case "login":
						String credentials[] = (String[]) request.getData();

						UserProvider provider1 = new UserProvider();

						send(new Response(provider1.authenticate(credentials[0], credentials[1])));
						break;
					case "get_session":
						send(new Response(UserProvider.getSession()));
						break;
					case "transaction_transfer":
						TransactionProvider tProvider = new TransactionProvider();
						Transaction transaction = (Transaction) request.getData();
						double balance = tProvider.getBalance(transaction.getSender());

						// Check if amount to transfer is greater than 0 and balance is greater than
						// amount
						if (balance >= transaction.getAmount() && transaction.getAmount() > 0) {
							if (transaction.getAmount() <= 1000000) {
								// Transfer money here
								if (tProvider.store(transaction) > 0)
									send(new Response(true));
								else {
									String message = "Something went wrong, what could've happened: "
											+ "Customer with that email doesn't exist or we did something";
									send(new Response(false, message));
								}
							} else {
								send(new Response(false,
										"You cannot transfer more than a million dollars at one time"));
							}
						} else if (transaction.getAmount() <= 0) {
							send(new Response(false, "You cannot send an amount less than or equal to zero"));
						} else {
							String message = "Cannot send $" + transaction.getAmount() + " to "
									+ transaction.getReceiver() + ", you only have $" + balance + " in your account";

							send(new Response(false, message));
						}
						break;
					case "transaction_inquiry":
						double balance2 = new TransactionProvider().getBalance(UserProvider.getSession().getId());

						send(new Response(true, balance2, "Your balance is $" + balance2));
						break;
					case "transaction_deposit":
						if (((Transaction) request.getData()).getAmount() <= 1000000) {
							if (new TransactionProvider().store((Transaction) request.getData()) > 0) {
								send(new Response(true));
							} else {
								send(new Response(false, "Could not deposit $"
										+ ((Transaction) request.getData()).getAmount() + " to your account."));
							}
						} else {
							send(new Response(false, "You cannot deposit more than a million dollars at one time."));
						}
						break;
					case "transaction_bill":
						TransactionProvider tProvider3 = new TransactionProvider();
						Transaction transaction3 = (Transaction) request.getData();
						double balance3 = tProvider3.getBalance(transaction3.getSender());

						// Check if amount to pay is greater than 0 and balance is greater than amount
						if (balance3 >= transaction3.getAmount() && transaction3.getAmount() > 0) {
							// pay bill here
							if (tProvider3.store(transaction3) > 0)
								send(new Response(true));
							else {
								String message = "Something went wrong paying your bill, try again.";
								send(new Response(false, message));
							}
						} else if (transaction3.getAmount() <= 0) {
							send(new Response(false, "You cannot pay an amount less than or equal to zero"));
						} else {
							String message = "Cannot pay $" + transaction3.getAmount() + " to "
									+ transaction3.getReceiver() + ", you only have $" + balance3 + " in your account";

							send(new Response(false, message));
						}

					case "store_message":
						List<String> cusMessage = (List<String>)request.getData();
						
						if(cusMessage.get(1).length() > 0) {
							UserProvider storeMessage = new UserProvider();
							boolean success = storeMessage.storeMessage(cusMessage);
							send(new Response(success));
						}else {
							send(new Response(false, "Cannot send blank message."));
						}
						break;
					case "values_for_chart":
							ManagerProvider mprovider = new ManagerProvider();
							
							send(new Response(true, mprovider.getChartValues()));
						break;
					case "get_transactions_for_customer":
						User customer = (User)request.getData();
						TransactionProvider findTransactions = new TransactionProvider();
						
						List<Transaction> trans = findTransactions.getCusTransactions(customer.getId());
						
						if (trans!=null) {
							send(new Response(trans));
						}else {
							JOptionPane.showMessageDialog(null, "No transactions returned.");
						}

						break;
					case "logout":
						boolean res = UserProvider.logout();
						send(new Response(res));
						logger.debug("logging out: "+ res);
					default:
						send(new Response(false, "Invalid server action"));
						break;
					}

				} catch (ClassNotFoundException e) {
					logger.error("Cannot locate class.");
					send(new Response(false, "Cannot locate class."));
				}
			} while (!request.getAction().equals("EXIT"));
			closeConnection();
			
		}catch(EOFException e) {
			//Ignore EOFException
		}catch(IOException e) {
			logger.error("Could not close or get streams.");
		}
	}

	public void getStreams() throws IOException {
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
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

	public void send(Response data) throws IOException {
		oos.writeObject(data);
	}

	public Request read() throws IOException, ClassNotFoundException {
		return (Request) ois.readObject();
	}

}
