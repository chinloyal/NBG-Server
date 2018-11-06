package database;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ManagerProvider extends SQLProvider
{
	private static final String TABLE_NAME = "transactions";
	//Implement a logger for every provider
	private static Logger logger = LogManager.getLogger(ManagerProvider.class);

	public ManagerProvider() {
		super();
	}

	@Override
	protected void initDatabase() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object get(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(Object item, int id) {
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
	public int store(Object item) {
		// TODO Auto-generated method stub
		return 0;
	}

	public double SumDebitBP()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 2";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
	public double SumDebitT()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 3";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
	public double SumDebitA()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 1";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
	public double SumCreditBP()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 2";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
	public double SumCreditT()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 3";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
	public double SumCreditA()
	{
		try
		{
		String query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 1";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);
		
		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		}catch(SQLException e)
		{
			logger.error("Unable to execute query");
		}
		return 0;
		
	}
}
