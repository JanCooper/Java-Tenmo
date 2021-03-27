package com.techelevator.tenmo.dao;

import java.util.List;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

public interface TransactionDAO {    
    Account getBalanceById(int id);
	List<User> getAllUsers(int id);
	Transfer transfer(Transfer transfer, int id);
	List<Transfer> getTransferHistory(int id);
}
