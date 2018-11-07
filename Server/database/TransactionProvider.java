package database;

import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import models.Transaction;

public class TransactionProvider extends SQLProvider<Transaction> {
	// TODO get balance of user
	private static Logger logger = LogManager.getLogger(TransactionProvider.class);
	public TransactionProvider() {
		super(); 
		try {
			String query = "CREATE IF NOT EXISTS VIEW credit_balances AS"+
					"SELECT u.id AS user_id, COALESCE(SUM(t.amount), 0) AS balance"+
					"FROM users u LEFT JOIN"+
					    "transactions t"+
					    "ON t.from_id = u.id AND"+
					       "t.card_type = 'credit'"+
					"GROUP BY u.id";
			
			statement = connection.createStatement();
			
			statement.execute(query);
			
			query = "CREATE IF NOT EXISTS VIEW debit_balances AS"+
					"SELECT u.id AS user_id, COALESCE(SUM(t.amount), 0) AS balance"+
					"FROM users u LEFT JOIN"+
					    "transactions t"+
					    "ON t.from_id = u.id AND"+
					       "t.card_type = 'debit'"+
					"GROUP BY u.id";
			
			statement = connection.createStatement();
			
			statement.execute(query);
		}catch(SQLException e) {
			logger.error("Unable to create credit/debit balance views.");
		}
	}

	protected void initDatabase() {
		// TODO Auto-generated method stub
		
	}


	public List<Transaction> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}


	public Transaction get(int id) {
		// TODO Auto-generated method stub
		return null;
	}


	public int update(Transaction item, int id) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int delete(int id) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int deleteMultiple(int[] id) {
		// TODO Auto-generated method stub
		return 0;
	}


	public int store(Transaction item) {
		// TODO Auto-generated method stub
		return 0;
	}

}
