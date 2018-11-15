package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import enums.CardType;
import enums.TransactionType;
import models.Transaction;
import models.User;

public class TransactionProvider extends SQLProvider<Transaction> {
	private static final String TABLE_NAME = "transactions";
	private static Logger logger = LogManager.getLogger(TransactionProvider.class);

	public TransactionProvider() {
		super();
		initDatabase();
	}

	protected void initDatabase() {

	}

	public double getBalance(int user_id) {

		try {
			String query = "" + "SELECT users.id AS user_id, COALESCE(balances.balance, 0) AS balance FROM users\r\n"
					+ "	LEFT JOIN (SELECT  \r\n" + "	  all_users.user_id,  \r\n" + "	  	  SUM(CASE \r\n"
					+ "	         WHEN t.transaction_type_id = 1 AND all_users.user_id = t.from_id \r\n"
					+ "	           THEN -ABS(t.amount) \r\n"
					+ "	         WHEN t.transaction_type_id = 1 AND all_users.user_id = t.to_id \r\n"
					+ "	           THEN ABS(t.amount) \r\n"
					+ "	         WHEN t.transaction_type_id = 3 AND all_users.user_id = t.from_id \r\n"
					+ "	           THEN ABS(t.amount)  \r\n"
					+ "	         WHEN t.transaction_type_id = 4 AND all_users.user_id = t.from_id \r\n"
					+ "	           	THEN -ABS(t.amount) \r\n" + "	         ELSE 0 \r\n" + "	      END \r\n"
					+ "	    	  ) AS balance  \r\n" + "		FROM transactions AS t  \r\n" + "		JOIN ( \r\n"
					+ "	      	SELECT from_id AS user_id FROM transactions  \r\n" + "	      	UNION  \r\n"
					+ "	      	SELECT to_id FROM transactions \r\n" + "	    	 ) AS all_users  \r\n"
					+ "	  	ON t.from_id = all_users.user_id OR  \r\n" + "	     	t.to_id = all_users.user_id \r\n"
					+ "		GROUP BY all_users.user_id) AS balances \r\n" + "	ON balances.user_id = users.id \r\n"
					+ "WHERE user_id = " + user_id + " AND users.type = 'customer'";

			statement = connection.createStatement();

			resultSet = statement.executeQuery(query);

			if (resultSet.next()) {
				return resultSet.getDouble("balance");
			}
		} catch (SQLException e) {
			logger.error("Unable user to get balance.");
		}

		return 0;
	}

	public List<Transaction> selectAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public Transaction getBy(String field, String value) {

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

	public int store(Transaction transaction) {
		try {
			String query = "INSERT INTO " + TABLE_NAME
					+ " (from_id, to_id, transaction_type_id, amount, description, card_type) "
					+ "VALUES (?, ?, ?, ?, ?, ?)";
			preparedStatement = connection.prepareStatement(query);

			if (transaction.getTransactionType().toString().toLowerCase().equals("transfer")) {
				UserProvider uProvider = new UserProvider();
				User receiver = uProvider.getBy("email", transaction.getReceiver());

				if (receiver != null) {
					if (receiver.getId() == UserProvider.getSession().getId())
						return 0;

					preparedStatement.setInt(2, receiver.getId());
					preparedStatement.setInt(3, 1); // TRANSFER id
					preparedStatement.setString(6, "credit");
				} else {
					return 0;
				}
			} else if (transaction.getTransactionType().toString().toLowerCase().equals("deposit")) {
				preparedStatement.setInt(2, transaction.getSender());
				preparedStatement.setInt(3, 3); // DEPOSIT id
				preparedStatement.setString(6, "debit");
			} else if (transaction.getTransactionType().toString().toLowerCase().equals("bill")) {
				preparedStatement.setNull(2, 0);
				preparedStatement.setInt(3, 4); // BILL id
				preparedStatement.setString(6, "credit");
			}

			preparedStatement.setInt(1, transaction.getSender());
			preparedStatement.setDouble(4, transaction.getAmount());
			preparedStatement.setString(5, transaction.getDescription());

			return preparedStatement.executeUpdate();

		} catch (SQLException e) {
			logger.error("Could not execute transaction.");
		}
		return 0;
	}

	public List<Transaction> getCusTransactions(int cusID) {
		List<Transaction> trans = new ArrayList<Transaction>();
		
		try {
			String query = "SELECT * "
					+ "FROM "+TABLE_NAME+" s "
					+ "INNER JOIN transaction_type t ON s.transaction_type_id=t.id "
					+ "WHERE s.from_id=? OR s.to_id=?";
			
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, cusID);
			preparedStatement.setInt(2, cusID);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				
				Transaction temp = new Transaction();
				temp.setId(resultSet.getInt("id")); // TRANSACTION ID
				temp.setAmount(resultSet.getFloat("amount")); // TRANSACTION AMOUNT
				temp.setDescription(resultSet.getString("description")); // TRANSACTION DESCRIPTION
				temp.setDate(resultSet.getDate("date")); // TRANSACTION DATE

				// TRANSACTION CARD TYPE (DEBIT/CREDIT)
				String cType = resultSet.getString("card_type");
				switch (cType) {
				case "debit":
					CardType cardType = CardType.DEBIT;
					temp.setCardType(cardType);
					break;
				case "credit":
					CardType cardType1 = CardType.CREDIT;
					temp.setCardType(cardType1);
					break;
				}
				
				String tType = resultSet.getString("t.name");
				// TRANSACTION TYPE
				switch (tType) {
				case "transfer":
					TransactionType type = TransactionType.TRANSFER;
					temp.setTransactionType(type);
					break;
				case "inquiry":
					TransactionType type1 = TransactionType.INQUIRY;
					temp.setTransactionType(type1);
					break;
				case "deposit":
					TransactionType type2 = TransactionType.DEPOSIT;
					temp.setTransactionType(type2);
					break;
				case "bill":
					TransactionType type3 = TransactionType.BILL;
					temp.setTransactionType(type3);
					break;
				} 
				trans.add(temp);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return trans;
	}
}
