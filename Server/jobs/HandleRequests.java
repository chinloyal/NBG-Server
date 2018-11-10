package jobs;


import java.io.EOFException;
import java.io.IOException;
import java.util.List;

import communication.Request;
import communication.Response;
import connection.Server;
import database.TransactionProvider;
import database.UserProvider;
import models.Transaction;
import models.User;

public class HandleRequests extends Server implements Runnable {
	public void run() {
		try {
			getStreams();
			Request request = null;

			do{
				try {
					request = (Request) ois.readObject();
					
					switch(request.getAction()) {
					case "register_user":
						UserProvider provider = new UserProvider();

						if(provider.store((User) request.getData()) > 0) {
							send(new Response(true, "Customer has been registered"));
						}else {
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
						
						// Check if amount to transfer is greater than 0 and balance is greater than amount
						if(balance >= transaction.getAmount() && transaction.getAmount() > 0) {
							if(transaction.getAmount() <= 1000000) {
								// Transfer money here
								if(tProvider.store(transaction) > 0)
									send(new Response(true));
								else {
									String message = "Something went wrong, what could've happened: "
											+ "User with that email doesn't exist or we did something";
									send(new Response(false, message));
								}
							}else {
								send(new Response(false, "You cannot transfer more than a million dollars at one time"));
							}
						}else if(transaction.getAmount() <= 0) {
							send(new Response(false, "You cannot send an amount less than or equal to zero"));
						}else {
							String message = "Cannot send $"+ transaction.getAmount() +
											" to " + transaction.getReceiver()
											+", you only have $" + balance + " in your account";
							
							send(new Response(false, message));
						}
						break;
					case "transaction_inquiry":
						double balance2 = new TransactionProvider().getBalance(UserProvider.getSession().getId());
						
						send(new Response(true, balance2, "Your balance is $"+ balance2));
						break;
					case "transaction_deposit":
						if(((Transaction) request.getData()).getAmount() <= 1000000) {
							if(new TransactionProvider().store((Transaction) request.getData()) > 0) {
								send(new Response(true));
							}else {
								send(new Response(false, "Could not deposit $" + ((Transaction) request.getData()).getAmount() + " to your account."));
							}
						}else {
							send(new Response(false, "You cannot deposit more than a million dollars at one time."));
						}
						break;
					case "transaction_bill":
						TransactionProvider tProvider3 = new TransactionProvider();
						Transaction transaction3 = (Transaction) request.getData();
						double balance3 = tProvider3.getBalance(transaction3.getSender());
						
						// Check if amount to pay is greater than 0 and balance is greater than amount
						if(balance3 >= transaction3.getAmount() && transaction3.getAmount() > 0) {
							// pay bill here
							if(tProvider3.store(transaction3) > 0)
								send(new Response(true));
							else {
								String message = "Something went wrong paying your bill, try again.";
								send(new Response(false, message));
							}
						}else if(transaction3.getAmount() <= 0) {
							send(new Response(false, "You cannot pay an amount less than or equal to zero"));
						}else {
							String message = "Cannot pay $"+ transaction3.getAmount() +
											" to " + transaction3.getReceiver()
											+", you only have $" + balance3 + " in your account";
							
							send(new Response(false, message));
						}

					case "store_message":
						List<String> cusMessage = (List<String>)request.getData();

						UserProvider storeMessage = new UserProvider();
						boolean success = storeMessage.storeMessage(cusMessage);
						send(new Response(success));
						break;
					default:
						send(new Response(false, "Invalid server action"));
						break;
					}
					
				} catch (ClassNotFoundException e) {
					logger.error("Cannot locate class.");
					send(new Response(false, "Cannot locate class."));
				}
			}while(!request.getAction().equals("EXIT"));
			closeConnection();
			
		}catch(EOFException e) {
			
		}catch(IOException e) {
			logger.error("Could not close or get streams.");
		}
	}

}
