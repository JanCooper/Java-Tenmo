package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.AccountUpdateException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

@Component
public class TransactionSqlDAO implements TransactionDAO {

    private JdbcTemplate jdbcTemplate;

    public TransactionSqlDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
 
    @Override
    public Account getBalanceById(int id){
    	Account account = null;
    	String sql= "SELECT balance "
    			+ "        , account_id "    			
    			+ "        , user_id "
    			+ "FROM accounts "
    			+ "WHERE user_id = ?;";
    	
    	SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
    	
    	if ( row.next() ) {
    		account = mapRowToAccount(row);
    	}    	
    	return account;
    }
    
	@Override
    public List<User> getAllUsers(int id){		
    	List<User> users = new ArrayList<User>();
    	String sql= "SELECT u.user_id "
    			+ "        , username "
    			+ "FROM users AS U "
    			+ "JOIN accounts AS A "
    			+ "		ON U.user_id = A.user_id "
    			+ "WHERE U.user_id != ?;";
    	
    	SqlRowSet row = jdbcTemplate.queryForRowSet(sql,id);
    	
    	while(row.next()) { 
    		User user = mapRowToUser(row);
    		users.add(user);
    	}    	
    	return users;
    }
	
	@Override
	public Transfer transfer(Transfer transfer, int id) {
		int transferFromId = 0;
		int transferToId = 0;
		int otherUserId = transfer.getOtherUser().getId().intValue();
		
		if(transfer.getType().equals("Send")) {
			transferFromId = id;
			transferToId = otherUserId;
		}
		if(transfer.getType().equals("Request")) {
			transferFromId = otherUserId;
			transferToId = id;
		}		

		BigDecimal amount = transfer.getAmount();
		boolean okToTransfer = checkBalance(transferFromId, amount);
		
		if(okToTransfer == true) {
			int type = transfer.getTypeId();
			int status = transfer.getStatusId();
			int nextVal = getNextVal("seq_transfer_id");
			
			String sql = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) "
					+ "VALUES(?, ?, ?, ?, ?, ?);";
			
			try {
				jdbcTemplate.update(sql, nextVal, type, status, transferFromId, transferToId, amount);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// verify insert succeeded
			String sql2 = "SELECT transfer_id "
					+ "		, transfer_status_id"
					+ "		, transfer_type_id"
					+ "		, account_from "
					+ "        , account_to "
					+ "        , amount "
					+ "FROM transfers "
					+ "WHERE transfer_id = ?;";
					
			SqlRowSet row2 = jdbcTemplate.queryForRowSet(sql2, nextVal);

			Transfer verifyTransfer = new Transfer();
			while(row2.next()) {
				verifyTransfer = mapRowToTransfer(row2);
			}
			
			boolean updateTransferTo = false;
			boolean updateTransferFrom = false;
			BigDecimal negative = BigDecimal.valueOf(-1.00);
			
			if (verifyTransfer.getStatusId() == status && verifyTransfer.getAccountFrom() == transferFromId && verifyTransfer.getAccountTo() == transferToId) {
				// If DB insert  succeeded and transfer is approved, update account balances
				if(status == 2) {
					try{
						updateTransferTo = updateBalance(transferToId, amount);
						BigDecimal negativeAmount = amount.multiply(negative) ;
						updateTransferFrom = updateBalance(transferFromId, negativeAmount);
						
						System.out.println("both balances updated");
						if (updateTransferTo == true && updateTransferFrom == true) {
							System.out.println("Setting response status");
							transfer.setResponseStatus("Transfer completed successfully");
						}
					} catch (Exception e) {
						e.printStackTrace();
						System.out.println("Account update failed.");
					}
				}			
			} else {
				transfer.setResponseStatus("Failed to transfer funds. Please contact Administrator.");
				transfer.setStatusId("Rejected");
				// TODO add logging for why transfer failed
			}
		} else {
			transfer.setResponseStatus("Transfer refused. Insufficient Funds.");
		}		
		return transfer;		
	}
	
	@Override
    public List<Transfer> getTransferHistory(int id){		
    	List<Transfer> transfers = new ArrayList<Transfer>();
    	String sql= "SELECT transfer_id "
    			+ "        , transfer_type_id "
    			+ "        , transfer_status_id "
    			+ "        , account_from "
    			+ "        , account_to "
    			+ "        , amount "
    			+ "FROM transfers "
    			+ "WHERE account_from = ? "
    			+ "        OR account_to = ?;  ";
    	
    	SqlRowSet row = jdbcTemplate.queryForRowSet(sql,id, id);
    	
    	while(row.next()) { 
    		Transfer transfer = mapRowToTransfer(row);
    		transfers.add(transfer);
    	}
    	
    	for (Transfer transfer : transfers) {
    		int otherUserId;
    		if(transfer.getType().equals("Send")) {
    			otherUserId = transfer.getAccountTo();
    		} else {
    			otherUserId = transfer.getAccountFrom();
    		}

    		User user = getUser(otherUserId);
    		transfer.setOtherUser(user); 
		} 
    	return transfers;    	
    }


/************************ Helper Methods ************************/
	
    private Account mapRowToAccount(SqlRowSet rs) {
    	Account account = new Account();
    	account.setAccountId(rs.getInt("account_id"));
    	account.setUserId(rs.getLong("user_id"));
    	account.setAccountBalance(rs.getBigDecimal("balance"));
        return account;
    }
   
	private Transfer mapRowToTransfer(SqlRowSet rs) {
        Transfer transfer = new Transfer();
        transfer.setId(rs.getInt("transfer_id"));
        transfer.setTypeId(rs.getInt("transfer_type_id"));
        transfer.setStatusId(rs.getInt("transfer_status_id"));
        transfer.setAccountFrom(rs.getInt("account_from"));
        transfer.setAccountTo(rs.getInt("account_to"));
        transfer.setAmount(rs.getBigDecimal("amount"));
        return transfer;
    } 	
    
    private User mapRowToUser(SqlRowSet row) {		
    	User user = new User();
    	user.setId(row.getLong("user_id"));
    	user.setUsername(row.getString("username"));    	
		return user;
	}
    
    private int getNextVal(String sequenceString) {
    	String sql = "SELECT nextval(' " + sequenceString + " ');";
    	SqlRowSet result = jdbcTemplate.queryForRowSet(sql);
    	result.next();
    	return result.getInt(1);
    }
    
    private boolean checkBalance(int userId, BigDecimal amount) {
    	BigDecimal balance = getBalance(userId);
    	boolean okToTransfer = false;

    	if(balance.compareTo(amount) >= 0) {
    		okToTransfer = true;
    	}
    	return okToTransfer;
    }
    
    private BigDecimal getBalance(int userId) {
    	String sql = "SELECT balance "
    			+ "FROM accounts "
    			+ "WHERE user_id = ?;";
    	SqlRowSet row = jdbcTemplate.queryForRowSet(sql, userId);
    	BigDecimal balance = BigDecimal.valueOf(0);
    	
    	if(row.next()) {
    		balance = row.getBigDecimal("balance");
    	}
    	return balance;
    }
    
    private boolean updateBalance(int userId, BigDecimal amount) throws AccountUpdateException {
    	BigDecimal startingBalance = getBalance(userId);
    	BigDecimal endingBalance = startingBalance.add(amount);
		
    	String sql = "UPDATE accounts "
    			+ "SET balance = ? "
    			+ "WHERE user_id = ?;";
    	
    	jdbcTemplate.update(sql, endingBalance, userId);
    	
    	BigDecimal verify = getBalance(userId);

    	boolean isUpdated = false;
    	if (endingBalance.compareTo(verify) >= 0) {
    		isUpdated = true;
    	} else {
    		throw new AccountUpdateException("Something went wrong when updating Account #" + userId + ". Please contact Administrator.");
    		// TODO  add logger with details 
    	}
    	return isUpdated;
    }
    
    private User getUser(int id) {
		String sql = "SELECT user_id "
				+ "		, username "
				+ "FROM users "
				+ "WHERE user_id = ?;";
		
		User user = null;
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
		if(row.next()) {
			 user = mapRowToUser(row);
		}		
		return user;		
    }
    
    private Account getAccount(int id) {
    	System.out.println("Getting account information");
    	Account account = new Account();
		String sql = "SELECT account_id "
				+ "        , user_id "
				+ "        , balance "
				+ "FROM accounts "
				+ "WHERE user_id = ?;";
		
		SqlRowSet row = jdbcTemplate.queryForRowSet(sql, id);
		while(row.next()) {
			account = mapRowToAccount(row);
		}
		return account;		
    }
    
    
}
