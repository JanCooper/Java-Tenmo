package com.techelevator.tenmo.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class TransactionService {
    
    private final String BASE_URL;
    public RestTemplate restTemplate = new RestTemplate();
   
	public TransactionService(String url) {		
		BASE_URL = url;
	}
	
	public Account viewBalance(AuthenticatedUser user)
	{
		int id = user.getUser().getId();
	    String token = user.getToken();
		String url = String.format("%s%d/account", BASE_URL , id);
		HttpEntity<?> entity = makeEntity(token);
		Account account = null;
		
		try 
		{
			account = restTemplate.exchange(url, HttpMethod.GET, entity, Account.class).getBody();
		} 
		catch (RestClientException e) 
		{
			// TODO throw custom exception
			e.printStackTrace();
		}		
		return account;
	}
		
	public List<User> getTransferUsers(AuthenticatedUser user){
		int id = user.getUser().getId();
		String token = user.getToken();
		String url = String.format("%s%d/transfer", BASE_URL , id);
		HttpEntity<?> entity = makeEntity(token);				
		List<User> users = new ArrayList<User>();
	    
		try 
		{
			ResponseEntity<User[]> transferToUsers = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class, id);
			users = Arrays.asList(transferToUsers.getBody());
		} 
		catch (RestClientException e) 
		{
			// TODO throw custom exception
			e.printStackTrace();
		}				
		return users;
	}
	
	public Transfer transfer(AuthenticatedUser user, Transfer transfer){ // use for sendBucks() and requestBucks()
		int id = user.getUser().getId();
		String token = user.getToken();
		String url = String.format("%s%d/transfer", BASE_URL , id);
		HttpEntity<?> entity = makeTransferEntity(transfer, token);				
		Transfer transferResponse = null;

		try 
		{
			transferResponse = restTemplate.postForObject(url, entity, Transfer.class);
		} 
		catch (RestClientException e) 
		{
			// TODO throw custom exception
			e.printStackTrace();
		}	
		return transferResponse;
	}
	
	public List<Transfer> getTransferHistory(AuthenticatedUser user){
		List<Transfer> transfers = new ArrayList<Transfer>();
		int id = user.getUser().getId();
		String token = user.getToken();
		String url = String.format("%s%d/transfer/history", BASE_URL , id);
		HttpEntity<?> entity = makeEntity(token);				

		try 
		{
			ResponseEntity<Transfer[]> transferHistory = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class, id);
			transfers = Arrays.asList(transferHistory.getBody());
			for (Transfer transfer : transfers) {
	    		System.out.println("---------------- TransactionService ---------------- ");
	            System.out.println("transfer_id " + transfer.getId());
	            System.out.println("transfer_type_id " + transfer.getTypeId());
	            System.out.println("transfer_type " + transfer.getType());
	            System.out.println("transfer_status_id " + transfer.getStatusId());
	            System.out.println("transfer_status " + transfer.getStatus()) ;
	            System.out.println("account_from " + transfer.getAccountFrom());
	            System.out.println("account_to " + transfer.getAccountTo());
	            System.out.println("amount " + transfer.getAmount());
	            System.out.println("user.username " + transfer.getOtherUser().getUsername());
	            System.out.println("user.id " + transfer.getOtherUser().getId());
	            System.out.println();
			}
		} 
		catch (RestClientException e) 
		{
			// TODO throw custom exception
			e.printStackTrace();
		}				
		return transfers;		
	}
	
	public Transfer viewTransferById(int id){
		// TODO implement method
		Transfer returnValue = new Transfer();
		return returnValue;	
	}
	
	public String approveTransferRequest(int approvalCode){
		// TODO implement method
		String returnValue = "";
		return returnValue;
	}  
    
	/************************ Entity methods ************************/
	
	private HttpEntity<?> makeEntity(String token)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return entity;
	} 
	
	private HttpEntity<?> makeTransferEntity(Transfer transfer, String token)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.setBearerAuth(token);
		HttpEntity<?> entity = new HttpEntity<>(transfer, headers);
		return entity;
	} 
}
