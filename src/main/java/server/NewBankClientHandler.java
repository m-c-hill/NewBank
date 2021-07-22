package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NewBankClientHandler extends Thread {

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;

	public NewBankClientHandler(Socket s) throws IOException {
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	// Method that prompts the user to enter their username and password
	// Returns a UserCredential object
	private UserCredentials takeCredentials(BufferedReader in, PrintWriter out) throws IOException {
		// ask for user name
		out.println("Enter Username");
		String username = in.readLine();
		// ask for password
		out.println("Enter Password");
		String password = in.readLine();
		out.println("Checking Details...");

		UserCredentials uc = new UserCredentials(username, password);

		return uc;
	}

	// While this chunk of code works, it's best to encapsulate the sign-up functionality in a different class
	private String takeFirstName(){
		out.println("Please enter your first name:");
		String fName = InputProcessor.takeValidInput("letters", in, out);
		return fName;
	}

	private String takeLastName(){
		out.println("Please enter your last name:");
		String lName = InputProcessor.takeValidInput("letters", in, out);
		return lName;
	}

	private String takeSsn(){
		out.println("Please enter your social security number:");
		String ssn = InputProcessor.takeValidInput("numbers", in, out);
		return ssn;
	}

	private String takePob(){
		out.println("Please enter your place of birth:");
		String pob = InputProcessor.takeValidInput("letters", in, out);
		return pob;
	}

	private String takeDob(){
		out.println("Please enter your date of birth (DD/MM/YYYY):");
		String dob = InputProcessor.takeValidInput("date", in, out);
		return dob;
	}

	private String takeEmail(){
		out.println("Please enter your email address:");
		String email = InputProcessor.takeValidInput("email", in, out);
		return email;
	}

	private String takePhoneNum(){
		out.println("Please enter your phone number (must start with a 0 followed by 10 digits):");
		String phoneNum = InputProcessor.takeValidInput("phonenumber", in, out);
		return phoneNum;
	}

	private String takeAddress(){
		out.println("Please enter your first address line:");
		String firstAddress = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your second address line:");
		String secondAddress = InputProcessor.takeValidInput("letters", in, out);

		return String.format(firstAddress + "\n" + secondAddress);
	}

	// Creating a new Customer object
	private Customer createCustomer(){
		String fName = takeFirstName();
		String lName = takeLastName();
		String ssn = takeSsn();
		String dob = takeDob();
		String pob = takePob();
		String email = takeEmail();
		String phoneNum = takePhoneNum();
		String address = takeAddress();

		Customer nc = new Customer(fName, lName, ssn, dob, pob, email, phoneNum, address);

		return nc;
	}

	// Adding the customer object to bank.customers<String, Customer> HashMap
	private void registerCustomer(Customer c){
		this.bank.addCustomer(c);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {
			// This loop will ensure that the user will always have the option to exit back to the welcome screen
			// User should execute "MENU" command
			UserCredentials uc;
			while (true) {
				// A welcome screen offering one option to login and another to register
				out.println("Please choose an option:\n1. Login as Customer\n2. Register for a New Customer Account\n3. Login as Admin");
				switch (in.readLine()) {
					case "1":
						uc = takeCredentials(in, out);
						CustomerID customer = bank.checkCustomerLogInDetails(uc.getUsername(), uc.getPassword());
						// if the user is authenticated then get requests from the user and process them
						if (customer != null) {
							out.println("Login Successful.");
							while (true) {
								out.println("What do you want to do?"
										+ "\n1. Show my accounts"
										+ "\n2. Withdraw amount"
										+ "\n3. Deposit amount "
										+ "\n4. Create a new account"
										+ "\n5. Request a loan"
										+ "\n6. Go back to the main menu");
								String request = in.readLine();
								if (request.equals("6")) {
									break;
								}
								System.out.println("Request from " + customer.getKey());
								String responce = bank.processCustomerRequest(customer, request, in, out);
								out.println(responce);
							}
						} else {
							out.println("Login Failed");
						}
						break;

					// You can use this for account creation? (Register)
					case "2":
						registerCustomer(createCustomer());
						out.println("User registered successfully.");
						break;
					
					case "3":
						uc = takeCredentials(in, out);
						String admin = bank.checkAdminLogInDetails(uc.getUsername(), uc.getPassword());

						if (admin != null) {
							out.println("Login successful.");
							while(true){
								out.println("What do you want to do:"
										+ "\n1. Check loans list"
										+ "\n2. Accept/Decline a loan request"
										+ "\n3. Go back to the main menu");
								
								String request = in.readLine();
								if (request.equals("3")) {
									break;
								}

								System.out.println("Request from " + admin);

								String responce = bank.processAdminRequest(admin, request, in, out);
								out.println(responce);
							}
						}
						else{
							out.println("Login failed.");
						}
				}
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				Thread.currentThread().interrupt();
			}
		}
	}

}
