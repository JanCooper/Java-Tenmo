package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Transfer {
	private int id;
	private User  otherUser;
	private BigDecimal amount;
	private int status_id;
	private int type_id;
	private int account_to;
	private int account_from;
	private String response_status;	
	
	public Transfer() {}
	
	public Transfer(BigDecimal amount, String type, String status, int chosenId, int userId) {
		this.amount = amount;
		setTypeId(type);
		setStatusId(status);
		this.account_to = chosenId;
		this.account_from = userId;
	}
	
	public Transfer(int id, User user, BigDecimal amount, int status_id, int type_id, int account_to, int account_from, String response_status) {
		this.id = id;
		otherUser = user;
		this.amount = amount;
		setTypeId(type_id);
		setStatusId(status_id);
		this.account_to = account_to;
		this.account_from = account_from;
		this.response_status = response_status;
	}

	public void setId(long id) {
		this.id = (int)id;
	}
	
	public void setOtherUser(User otherUser) {
		this.otherUser = otherUser;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public void setStatusId(int id) {
		this.status_id = id;
	}
	
	public void setStatusId(String status) {
		if(status.equals("Pending")) {
			status_id = 1;
		} else if(status.equals("Approved")) {
			status_id = 2;
		} else if (status.equals("Rejected")) {
			status_id = 3;
		}
	}
	
	public void setTypeId(int id) {
		this.type_id = id;
	}
	
	public void setTypeId(String  type) {
		if(type.equals("Request")) {
			type_id = 1;
		} else if (type.equals("Send")) {
			type_id = 2;
		} 
	}
	
	public void setResponseStatus(String response) {
		response_status = response;
	}
	
	public void setAccountTo(int id) {
		account_to = id;
	}
	
	public void setAccountFrom(int id) {
		account_from = id;
	}
		
	public int getId() {
		return id;
	}
	
	public User getOtherUser() {
		return otherUser;
	}

	public BigDecimal getAmount() {
		return amount;
	}
	
	public int getStatusId() {
		return status_id;
	}
	
	public int getTypeId() {
		return type_id;
	}
	
	public String getStatus() {
		String status = "";
		if(status_id == 1) {
			status = "Pending";
		} else if(status_id == 2) {
			status = "Approved";
		} else if (status_id == 3) {
			status = "Rejected";
		}
		return status;
	}
	
	public String getType() {
		String type = "";
		if(type_id == 1) {
			type = "Request";
		} else if (type_id == 2) {
			type = "Send";
		} 		
		return  type;
	}
	
	public String getResponseStatus() {
		return response_status;
	}
	
	public int getAccountTo() {
		return account_to;
	}
	
	public int getAccountFrom() {
		return account_from;
	}
}
