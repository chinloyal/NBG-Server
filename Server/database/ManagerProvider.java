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
	public double TransTypeGraph(String type)
	{
		try
		{
		String query = "";
		System.out.println(type);
		switch(type) 
		{
		case "SumDebitBP" :  query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 2";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}

		case "SumDebitT" :  query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 3";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		case "SumDebitA" :  query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 1";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		case "SumCreditBP" :  query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 2";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		case "SumCreditT" :  query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 3";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}
		
		case "SumCreditA" :   query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 1";
		statement = connection.createStatement();
		
		resultSet = statement.executeQuery(query);

		if(resultSet.next())
		{
			return resultSet.getDouble(1);
		}	
		
		}
		
		/*if(type.equals("SumDebitBP"))
		{
			 query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 2";
		}else if(type.equals("SumDebitT"))
		{
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 3";
		}else if(type.equals("SumDebitA"))
		{
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 1";
		}else if(type.equals("SumCreditBP"))
		{
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 2";
		}else if(type.equals("SumCreditT"))
		{
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 3";
		}
		else if(type.equals("SumCreditA"))
		{
			 query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 1";
		}else
		{
			query = "0";
			System.out.println("no query processeed");
		}
		
		switch("") {}*/
		
		System.out.println("test");
	
		
	
	}catch(SQLException e)
	{
		logger.error("SQL Exception thrown");
	}
	return 0.0;
	}
	
}
