package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Account {
	private int account_id;
	private int user_id;
	private BigDecimal accountBalance;
	
	public void setAccountId(int id) {
		account_id = id;
	}
	
	public void setUserId(long id) {
		user_id = (int)id;
	}
	
	public void setAccountBalance(BigDecimal balance) {
		accountBalance = balance;
	}
	
	public int getAccountId() {
		return account_id;
	}
	
	public int getUserId() {
		return user_id;
	}
	
	public BigDecimal getAccountBalance() {
		return accountBalance;
	}
}
