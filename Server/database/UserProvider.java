package database;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

import models.Photo;
import models.User;

public class UserProvider extends SQLProvider<User> {
	private static final String TABLE_NAME = "users";
	private static final String SESSION_FILE = "session.dat";
	//Implement a logger for every provider
	private static Logger logger = LogManager.getLogger(UserProvider.class);

	public UserProvider() {
		super();
//		initDatabase();
	}

	@Override
	protected void initDatabase() {
		try {
			String query = "" +
					"INSERT IGNORE INTO `query_type`(`id`, `name`) VALUES \r\n" + 
					"(1, 'balance inquiry'), \r\n" + 
					"(2, 'transfer'), \r\n" + 
					"(3, 'payment'), \r\n" + 
					"(4, 'support'), \r\n" + 
					"(5, 'other')";
			statement = connection.createStatement();
			statement.executeUpdate(query);
		}catch(SQLException e) {
			logger.error("Unable to init database.");
		}
	}

	@Override
	public List<User> selectAll() {
		List<User> users = null;
		try {
			users = new ArrayList<User>();

			String query = "SELECT * FROM " + TABLE_NAME;
			statement = connection.createStatement();

			logger.info("Executing " + query);

			resultSet = statement.executeQuery(query);

			while (resultSet.next()) {
				User user = new User(resultSet.getInt("id"), resultSet.getString("first_name"),
						resultSet.getString("last_name"), resultSet.getString("type"), resultSet.getString("email"),
						resultSet.getString("password"));

				users.add(user);
			}

			logger.debug("Retrieved " + users.size() + " user(s).");
		} catch (SQLException e) {
			logger.error("Failed to execute select all query for Table: " + TABLE_NAME);
		}

		return users;
	}

	public User getBy(String field, String value) {
		try {
			String query = "SELECT u.*, p.file FROM " + TABLE_NAME + " u" +
							" LEFT JOIN photos p" +
							" ON p.user_id = u.id" +
							" WHERE " + field + " = ?";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, value);
			
			resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				User user = new User();
				
				user.setId(resultSet.getInt("id"));
				user.setEmail(resultSet.getString("email"));
				user.setFirstName(resultSet.getString("first_name"));
				user.setLastName(resultSet.getString("last_name"));
				user.setPassword(resultSet.getString("password"));
				user.setType(resultSet.getString("type"));
				
				if(user.getType().equals("customer")) {
					Photo photo = new Photo(resultSet.getString("file"));
					
					user.setPhoto(photo);
				}
				
				return user;
			}else {
				return null;
			}
			
		}catch(SQLException e) {
			logger.error("Could not get item from the " + TABLE_NAME + " table with field: " + field + " and value: "+ value);
		}
		return null;
	}

	@Override
	public int update(User item, int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int deleteMultiple(int[] id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int store(User user) {
		try {
			String query = "INSERT INTO " + TABLE_NAME + "(first_name, last_name, email, type, password) "
					+ "VALUES(?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

			preparedStatement.setString(1, user.getFirstName());
			preparedStatement.setString(2, user.getLastName());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getType());
			preparedStatement.setString(5, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));

			int userRowsAffected = preparedStatement.executeUpdate();

			if (user.getType().equals("customer") && user.getPhoto() != null) {
				Photo profilePhoto = user.getPhoto();
				int user_id = getLastInsertedId(preparedStatement);

				query = "INSERT INTO photos (file, user_id) " + "VALUES ('" + profilePhoto.getName() + "', " + user_id
						+ ")";
				statement = connection.createStatement();

				statement.executeUpdate(query);
			}

			return userRowsAffected;
		} catch (SQLException e) {
			logger.error("Failed to store user.", e.getMessage());
			// e.printStackTrace();
		}

		return 0;
	}

	public boolean authenticate(String email, String userPassword) {
		User user = getBy("email", email);
		if(user != null) {
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(SESSION_FILE));
				oos.writeObject(user);

			} catch (IOException e) {
				logger.error("Could not store session.");
			}finally {
				try {
					oos.close();
				}catch(IOException | NullPointerException e) {
					logger.error("Could not close session output stream.");
				}

			}

			return BCrypt.checkpw(userPassword, user.getPassword());
		}
		return false;
	}
	
	public static User getSession() {
		User user = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(SESSION_FILE));
			
			user = (User) ois.readObject();
		}catch(IOException | ClassNotFoundException | ClassCastException e) {
			logger.error("Could not retrieve session.");
		}finally {
			try {
				ois.close();
			}catch(IOException | NullPointerException e) {
				logger.error("Could not close session input stream.");
			}

		}
		
		return user;
	}

	public boolean storeMessage(List<String> cusMessage) {
		boolean success = false;
		String message = cusMessage.get(1);
		String queryType = cusMessage.get(2);
		int query_type_id = -1;

		try {
			// There's no need to add query types to the table every time you send a message, just add the query types to the database manually
			/*// Add Query to query_type Table
			String query = "INSERT INTO query_type (name) VALUES(?)";
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, queryType);

			int numRowsAffected = preparedStatement.executeUpdate();*/
			
			String query3= "SELECT id FROM query_type WHERE name = ?";
			preparedStatement = connection.prepareStatement(query3);
			preparedStatement.setString(1, queryType);
			
			resultSet = preparedStatement.executeQuery();

			if (resultSet.next()) {
				query_type_id = resultSet.getInt(1);
			}
			
			if(query_type_id != -1) {
				// Add Message to messages table
				User user = getSession();

				String query2 = "INSERT INTO messages (user_id, body, query_type_id) VALUES(?, ?, ?)";
				preparedStatement = connection.prepareStatement(query2);

				preparedStatement.setInt(1, user.getId());
				preparedStatement.setString(2, message);
				preparedStatement.setInt(3, query_type_id);

				int numRowsAffected2 = preparedStatement.executeUpdate();

				if (numRowsAffected2>0) {
					success = true;
				}			
			}else {
				success = false;
			}
		} catch (SQLException e) {
			success = false;
			logger.error("Failed to Store Message.", e.getMessage());
		}
		return success;
	}
}
