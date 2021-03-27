package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.TransactionDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransactionController {
	@Autowired
	private TransactionDAO dao;	
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping("{id}/account")
	public Account getBalance(@PathVariable int id, Principal principal)  {
		//User user = userDAO.findByUsername(principal.getName());
		return dao.getBalanceById(id);		
	}
	
	@GetMapping("{id}/transfer")
	public List<User> getAllUsers(@PathVariable int id){
		return  dao.getAllUsers(id);
	}
	
	@PostMapping("{id}/transfer")
	public Transfer Transfer(@RequestBody Transfer transfer, @PathVariable int id, Principal principal) {
		//User user = userDAO.findByUsername(principal.getName());
		return dao.transfer(transfer, id);
	}
	
	@GetMapping("{id}/transfer/history")
	public List<Transfer> getTransferHistory(@PathVariable int id, Principal principal){
		//User user = userDAO.findByUsername(principal.getName());
		return dao.getTransferHistory(id);
	}
	
	@GetMapping("{id}/transfer/{id}")
	public Transfer getTransferById(@PathVariable int id) {
		// TODO write method logic
		Transfer returnValue = new Transfer();
		return returnValue;
	}	

	@GetMapping("{id}/transfer/pending")
	public List<Transfer> getTransferPending(@RequestParam(required = false) String status, @RequestParam int id){
		// TODO write method logic
		// if no status string, return all by current user ID
		List<Transfer> returnValue = new ArrayList<Transfer>();
		return returnValue;	
	}
	
	@PutMapping("{id}/transfer/pending/approval")
	public String TransferApproval(@RequestBody Transfer transfer) {
		// TODO write method logic
		String returnValue = "";
		return returnValue;
	}
	
}
