package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
	private int id;
	private User  otherUser;
	private BigDecimal amount;
	private int status_id;
	private int type_id;
	private int account_from;
	private int account_to;
	private String response_status;
	
	public Transfer() {}
	
	public Transfer(User otherUser, BigDecimal amount, int type, int status, String response_status) {
		this.otherUser = otherUser;
		this.amount = amount;
		this.type_id = type;
		this.status_id = status;
		this.response_status = response_status;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public void setOtherUser(User otherUser) {
		this.otherUser = otherUser;
	}
	
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	
	public void setStatusId(int status) {
		status_id = status;
	}
	
	public void setTypeId(int  type) {
		type_id = type;
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
	
	public void setTypeId(String  type) {
		if(type.equals("Request")) {
			type_id = 1;
		} else if (type.equals("Send")) {
			type_id = 2;
		} 
	}
	
	public void setAccountFrom(int id) {
		account_from = id;
	}
	
	public void setAccountTo(int id) {
		account_to = id;
	}
	
	public void setResponseStatus(String response) {
		response_status = response;
	}
	
	public int getId() {
		return id;
	}
	
	public User  getOtherUser() {
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
	
	public int getAccountFrom() {
		return account_from;
	}
	
	public int getAccountTo() {
		return account_to;
	}
	
	public String getResponseStatus() {
		return response_status;
	}
}
