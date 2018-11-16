package database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import models.ChartVals;

public class ManagerProvider extends SQLProvider<Object>
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
	public double transTypeGraph(String type)
	{
		try
		{
		String query = "";
		System.out.println(type);
		switch(type){
		case "SumDebitBP" :  
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 2";
			statement = connection.createStatement();

			resultSet = statement.executeQuery(query);

			if(resultSet.next())
			{
				return resultSet.getDouble(1);
			}

		case "SumDebitT" :
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 3";
			statement = connection.createStatement();

			resultSet = statement.executeQuery(query);

			if(resultSet.next())
			{
				return resultSet.getDouble(1);
			}

		case "SumDebitA" : 
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'debit' AND transaction_type_id = 1";
			statement = connection.createStatement();

			resultSet = statement.executeQuery(query);

			if(resultSet.next())
			{
				return resultSet.getDouble(1);
			}

		case "SumCreditBP" :
			query =  "SELECT SUM(amount) FROM `transactions` WHERE card_type = 'credit' AND transaction_type_id = 2";
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



		}catch(SQLException e)
		{
			logger.error("SQL Exception thrown");
		}
		return 0.0;
	}
	
	public List<ChartVals> getChartValues(){
		try {
			String query =  "SELECT tt.name, SUM(amount) AS total, t.card_type FROM transactions t\r\n" + 
							"LEFT JOIN transaction_type tt\r\n" + 
							"ON tt.id = t.transaction_type_id\r\n" + 
							"GROUP BY tt.name";
			
			statement = connection.createStatement();
			resultSet = statement.executeQuery(query);
			List<ChartVals> chartValues = new ArrayList<ChartVals>();
			
			while(resultSet.next()) {
				ChartVals chart = new ChartVals();
				
				chart.setCardType(resultSet.getString("card_type"));
				chart.setTransactionType(resultSet.getString("name"));
				chart.setTotal(resultSet.getDouble("total"));
				
				chartValues.add(chart);
			}
			
			return chartValues;
		}catch(SQLException e) {
			logger.error("Could not get chart values.");
		}
		return null;
	}

	@Override
	public Object getBy(String field, String value) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
