package com.techelevator.view;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.print.attribute.HashAttributeSet;

import com.techelevator.tenmo.models.Transfer;
import com.techelevator.tenmo.models.User;

public class ConsoleService {

	private PrintWriter out;
	private Scanner in;

	public ConsoleService(InputStream input, OutputStream output) {
		this.out = new PrintWriter(output, true);
		this.in = new Scanner(input);
	}

	public Object getChoiceFromOptions(Object[] options) {
		Object choice = null;
		while (choice == null) {
			displayMenuOptions(options);
			choice = getChoiceFromUserInput(options);
		}
		out.println();
		return choice;
	}

	private Object getChoiceFromUserInput(Object[] options) {
		Object choice = null;
		String userInput = in.nextLine();
		try {
			int selectedOption = Integer.valueOf(userInput);
			if (selectedOption > 0 && selectedOption <= options.length) {
				choice = options[selectedOption - 1];
			}
		} catch (NumberFormatException e) {
			// eat the exception, an error message will be displayed below since choice will be null
		}
		if (choice == null) {
			out.println("\n*** " + userInput + " is not a valid option ***\n");
		}
		return choice;
	}

	private void displayMenuOptions(Object[] options) {
		out.println();
		for (int i = 0; i < options.length; i++) {
			int optionNum = i + 1;
			out.println(optionNum + ") " + options[i]);
		}
		out.print("\nPlease choose an option >>> ");
		out.flush();
	}

	public String getUserInput(String prompt) {
		out.print(prompt+": ");
		out.flush();
		return in.nextLine();
	}

	public Integer getUserInputInteger(String prompt) {
		Integer result = null;
		do {
			out.print(prompt+": \r\n");
			out.flush();
			String userInput = in.nextLine();
			try {
				result = Integer.parseInt(userInput);
			} catch(NumberFormatException e) {
				out.println("\n*** " + userInput + " is not valid ***\n");
			}
		} while(result == null);
		return result;
	}
	
	public BigDecimal getUserInputBigDecimal(String prompt) {
		BigDecimal amount = null;
		do {
			String value = getUserInput(prompt);
			try {
				amount = new BigDecimal(value);
			} catch (NumberFormatException e) {
				out.println("\n*** " + value + " is not valid ***\n");
			}
		} while(amount == null);
		return amount;
	}
	
	public void displayString(String string) {
		out.println("\r\n" + string);
	}
	
	public void displayUsers(List<User> users) {
		out.println("---------------------------------------------------");
		out.println("Users");		
		out.printf(" %5s     %-30s\r\n", "ID", "Name");
		out.println("---------------------------------------------------");
		
		for (User user : users) {
			int id = user.getId();
			String name = user.getUsername();
			out.printf(" %5d     %-30s\r\n", id, name);
		}
		out.println("---------------------------------------------------");
	}
		
	public void displayTransfers(List<Transfer> transfers) {		
		out.println("---------------------------------------------------");
		out.println("Transfers");
		out.printf( " %5s    %-30s    %s\r\n", "ID", "To/From", "Amount");
		out.println("---------------------------------------------------");
		
		for (Transfer transfer : transfers) {
			String type = transfer.getType();
			int  id = transfer.getId();
			String name = transfer.getOtherUser().getUsername();
			String amount = "$" + String.valueOf(transfer.getAmount());
			
			if(transfer.getStatus().equals("Approved")) {
				if(type.equals("Send")) {	
					name = "To: " + name;
				} else if(type.equals("Request")) {
					name =  "From: " + name;
				}
				out.printf(" %5s    %-30s    %s\r\n", id, name, amount);
			}
		}
		out.println("\r\n---------------------------------------------------");
	}
	
	public void displayTransferDetail(Transfer transfer, int id) {
		out.println("---------------------------------------------------");
		out.println("Transfer Details");
		out.println("---------------------------------------------------");
		
		int transferId = transfer.getId();
		int account_from = transfer.getAccountFrom();
		String from = "";
		if(account_from == id) {
			from = "Me";
		} else {
			from = transfer.getOtherUser().getUsername();
		}
		String to = "";
		int account_to = transfer.getAccountTo();
		if(account_to == id) {
			to = "Me";
		} else {
			to = transfer.getOtherUser().getUsername();
		}
		String type = transfer.getType();
		String status = transfer.getStatus();
		BigDecimal amount = transfer.getAmount();
		
		out.println(" Id: " + transferId);
		out.println(" From: " + from );
		out.println(" To: " + to);
		out.println(" Type: " + type);
		out.println(" Status: " + status);
		out.println(" Amount: $" + amount );
		out.println();
	}

}
