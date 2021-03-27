package com.techelevator.tenmo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.techelevator.tenmo.models.Account;
import com.techelevator.tenmo.models.AuthenticatedUser;
import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;
import com.techelevator.tenmo.models.UserCredentials;
import com.techelevator.tenmo.services.TransactionService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.AuthenticationServiceException;
import com.techelevator.view.ConsoleService;

public class App {

private static final String API_BASE_URL = "http://localhost:8080/";
    
    private static final String MENU_OPTION_EXIT = "Exit";
    private static final String LOGIN_MENU_OPTION_REGISTER = "Register";
	private static final String LOGIN_MENU_OPTION_LOGIN = "Login";
	private static final String[] LOGIN_MENU_OPTIONS = { LOGIN_MENU_OPTION_REGISTER, LOGIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	private static final String MAIN_MENU_OPTION_VIEW_BALANCE = "View your current balance";
	private static final String MAIN_MENU_OPTION_SEND_BUCKS = "Send TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS = "View your past transfers";
	private static final String MAIN_MENU_OPTION_REQUEST_BUCKS = "Request TE bucks";
	private static final String MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS = "View your pending requests";
	private static final String MAIN_MENU_OPTION_LOGIN = "Login as different user";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_VIEW_BALANCE, MAIN_MENU_OPTION_SEND_BUCKS, MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS, MAIN_MENU_OPTION_REQUEST_BUCKS, MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS, MAIN_MENU_OPTION_LOGIN, MENU_OPTION_EXIT };
	
    private AuthenticatedUser currentUser;
    private ConsoleService console;
    private AuthenticationService authenticationService;
    private TransactionService transactionService;
    
    public static void main(String[] args) {
    	App app = new App(new ConsoleService(System.in, System.out), new AuthenticationService(API_BASE_URL), new TransactionService(API_BASE_URL));
    	app.run();
    }

    public App(ConsoleService console, AuthenticationService authenticationService, TransactionService transactionService) {
		this.console = console;
		this.authenticationService = authenticationService;
		this.transactionService = transactionService;
    }

	public void run() {
		System.out.println("*********************");
		System.out.println("* Welcome to TEnmo! *");
		System.out.println("*********************");
		
		registerAndLogin();
		mainMenu();		
	}

	private void mainMenu() {
		while(true) {
			String choice = (String)console.getChoiceFromOptions(MAIN_MENU_OPTIONS);
			if(MAIN_MENU_OPTION_VIEW_BALANCE.equals(choice)) {
				viewCurrentBalance();
			} else if(MAIN_MENU_OPTION_VIEW_PAST_TRANSFERS.equals(choice)) {
				viewTransferHistory();
			} else if(MAIN_MENU_OPTION_VIEW_PENDING_REQUESTS.equals(choice)) {
				viewPendingRequests();
			} else if(MAIN_MENU_OPTION_SEND_BUCKS.equals(choice)) {
				sendBucks();
			} else if(MAIN_MENU_OPTION_REQUEST_BUCKS.equals(choice)) {
				requestBucks();
			} else if(MAIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else {
				// the only other option on the main menu is to exit
				exitProgram();
			}
		}
	}

	private void viewCurrentBalance() 
	{
		Account account = transactionService.viewBalance(currentUser);
		BigDecimal balance = account.getAccountBalance().setScale(2, RoundingMode.HALF_UP);
		String accountBalance = "Your current balance is: $" + balance;
		console.displayString(accountBalance);
	}

	private void sendBucks() {
		User user = currentUser.getUser();
		int userId =  user.getId();
		List<User> users = transactionService.getTransferUsers(currentUser);
		console.displayUsers(users);
		String footer =  "Enter ID of user you are sending to (0 to cancel) ";
		int chosenId = console.getUserInputInteger(footer);	
		BigDecimal amount;
		
		switch(chosenId) {
			case 0 : return;
			default : 
				boolean isOnList = false;
				for (User object : users) {
					if(object.getId() == chosenId) {
						isOnList = true;
						break;
					}
				}
				
				if(isOnList == false) {
					sendBucks();
				}
				amount = console.getUserInputBigDecimal("Enter Amount ");
			break;
		}
		
		amount = amount.setScale(2, RoundingMode.HALF_UP);
		Transfer transferObject = new Transfer(amount, "Send", "Approved", chosenId, userId);
		User otherUser = new User(chosenId);
		transferObject.setOtherUser(otherUser);		
		Transfer transferResponse = transactionService.transfer(currentUser, transferObject);
		console.displayString(transferResponse.getResponseStatus());
		viewCurrentBalance();
	}
	
	private void viewTransferHistory() {
		List<Transfer> transfers = transactionService.getTransferHistory(currentUser);
		console.displayTransfers(transfers);
		String footer = "Please enter transfer ID to view details (0 to cancel)";
		int chosenId = console.getUserInputInteger(footer);	
		boolean isOnList = false;
		Transfer display = null;
		
		switch(chosenId) {
			case 0 : return;
			default : 
				for (Transfer object : transfers) {
					if(object.getId() == chosenId) {
						display = object;
						isOnList = true;
						break;
					}
				}
		}
		
		if(isOnList == false) {
			viewTransferHistory();
		}		
		console.displayTransferDetail(display, currentUser.getUser().getId());		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}
	
	private void exitProgram() {
		System.exit(0);
	}

	private void registerAndLogin() {
		while(!isAuthenticated()) {
			String choice = (String)console.getChoiceFromOptions(LOGIN_MENU_OPTIONS);
			if (LOGIN_MENU_OPTION_LOGIN.equals(choice)) {
				login();
			} else if (LOGIN_MENU_OPTION_REGISTER.equals(choice)) {
				register();
			} else {
				// the only other option on the login menu is to exit
				exitProgram();
			}
		}
	}

	private boolean isAuthenticated() {
		return currentUser != null;
	}

	private void register() {
		System.out.println("Please register a new user account");
		boolean isRegistered = false;
        while (!isRegistered) //will keep looping until user is registered
        {
            UserCredentials credentials = collectUserCredentials();
            try {
            	authenticationService.register(credentials);
            	isRegistered = true;
            	System.out.println("Registration successful. You can now login.");
            } catch(AuthenticationServiceException e) {
            	System.out.println("REGISTRATION ERROR: "+e.getMessage());
				System.out.println("Please attempt to register again.");
            }
        }
	}

	private void login() {
		System.out.println("Please log in");
		currentUser = null;
		while (currentUser == null) //will keep looping until user is logged in
		{
			UserCredentials credentials = collectUserCredentials();
		    try {
				currentUser = authenticationService.login(credentials);
			} catch (AuthenticationServiceException e) {
				System.out.println("LOGIN ERROR: "+e.getMessage());
				System.out.println("Please attempt to login again.");
			}
		}
	}
	
	private UserCredentials collectUserCredentials() {
		String username = console.getUserInput("Username");
		String password = console.getUserInput("Password");
		return new UserCredentials(username, password);
	}
	
	
}
