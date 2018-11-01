package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public abstract class SQLProvider<T> {

	protected Connection connection = null;
	protected Statement statement = null;
	protected ResultSet resultSet = null;
	protected PreparedStatement preparedStatement = null;
	
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	
	private Logger logger = LogManager.getLogger(SQLProvider.class);
	
	public SQLProvider() {
		try {
			logger.info("Attempting to connect to the database...");
			Class.forName(DRIVER); //The newInstance method is deprecated in java 10
			
			String url = "jdbc:mysql://localhost:3306/ap_groupproject";
			
			connection = DriverManager.getConnection(url, "root", "");
			
			initDatabase();
			logger.info("Connected to database");
			
		}catch(SQLException e) {
			logger.error("Could not connect to database", e.getMessage());
		}catch (ClassNotFoundException e) {
			logger.error("Failed to load JDBC Driver", e.getMessage());
		}catch (NullPointerException e) {
			logger.error("Could not find database", e.getMessage());
		}//Since the newInstance method is deprecated, we no longer have to catch an IllegalAccessException or an InstantiationException
	}
	
	protected abstract void initDatabase();
	
	public abstract List<T> selectAll();
	
	/**
	 * Get a row of data from a table
	 * @param id - ID of model in table
	 * @return
	 */
	public abstract T get(int id);
	
	public abstract int update(T item, int id);
	
	public abstract int delete(int id);
	
	public abstract int deleteMultiple(int[] id);
	
	public abstract int store(T item);
	
	protected int getLastInsertedId(Statement statement) throws SQLException {
		ResultSet rs = statement.getGeneratedKeys();
		int id = 0;
		
		if(rs.next()) {
			id = rs.getInt(1);
		}
		
		return id;
			
	}
	
}
