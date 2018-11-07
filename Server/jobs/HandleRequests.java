package jobs;


import java.io.IOException;

import communication.Request;
import communication.Response;
import connection.Server;
import database.UserProvider;
import models.User;

public class HandleRequests extends Server implements Runnable {
	public void run() {
		try {
			getStreams();
			Request request = null;

			do{
				try {
					request = (Request) ois.readObject();

					if(request.getAction().equals("register_user")){
						UserProvider provider = new UserProvider();

						if(provider.store((User) request.getData()) > 0) {
							send(new Response(true, "Customer has been registered"));
						}else {
							send(new Response(false));
						}

					}else if(request.getAction().equals("login")){
						String credentials[] = (String[]) request.getData();
						
						UserProvider provider = new UserProvider();
					
						send(new Response(provider.authenticate(credentials[0], credentials[1])));
					}
				} catch (ClassNotFoundException e) {
					logger.error("Cannot locate class.");
				}catch(ClassCastException e) {
					logger.error("Could not cast class.");
				}
			}while(!request.getAction().equals("EXIT"));
			closeConnection();
			
		}catch(IOException e) {
			logger.error("Could not close or get streams.");
		}
	}

}
