package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;

import models.Photo;
import models.User;

public class UserProvider extends SQLProvider<User> {
	private static final String TABLE_NAME = "users";
	//Implement a logger for every provider
	private static Logger logger = LogManager.getLogger(UserProvider.class);

	public UserProvider() {
		super();
	}

	@Override
	protected void initDatabase() {
		// TODO Auto-generated method stub
		
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
			
			while(resultSet.next()) {
				User user = new User(
					resultSet.getInt("id"),
					resultSet.getString("first_name"),
					resultSet.getString("last_name"),
					resultSet.getString("type"),
					resultSet.getString("email"),
					resultSet.getString("password")
				);
				
				users.add(user);
			}
			
			logger.debug("Retrieved " + users.size() + " users.");
		}catch(SQLException e) {
			logger.error("Failed to execute select all query for Table: " + TABLE_NAME);
		}
		
		
		
		return users;
	}

	@Override
	public User get(int id) {
		// TODO Auto-generated method stub
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
			String query = "INSERT INTO " + TABLE_NAME + 
							"(first_name, last_name, email, type, password) " +
							"VALUES(?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
			
			preparedStatement.setString(1, user.getFirstName());
			preparedStatement.setString(2, user.getLastName());
			preparedStatement.setString(3, user.getEmail());
			preparedStatement.setString(4, user.getType());
			preparedStatement.setString(5, BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
			
			int userRowsAffected = preparedStatement.executeUpdate();
			
			if(user.getType().equals("customer") && user.getPhoto() != null) {
				Photo profilePhoto = user.getPhoto();
				int user_id = getLastInsertedId(preparedStatement);
				
				query = "INSERT INTO photos (file, user_id) "+
						"VALUES ('"+ profilePhoto.getName() +"', "+ user_id +")";
				statement = connection.createStatement();
				
				statement.executeUpdate(query);
			}
			
			return userRowsAffected;
		}catch(SQLException e) {
			logger.error("Failed to store user", e.getMessage());
//			e.printStackTrace();
		}
		
		return 0;
	}

}
