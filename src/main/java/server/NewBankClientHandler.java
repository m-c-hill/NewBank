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
	private Socket socket;

	public NewBankClientHandler(Socket s) throws IOException {
		socket = s;
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

	// TODO: move customer creation to the customer class or a new registration class

	// While this chunk of code works, it's best to encapsulate the sign-up functionality in a different class


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
										+ "\n6. View my loan status"
										+ "\n7. Pay back my loan"
										+ "\n8. Go back to the main menu");
								String request = in.readLine();
								if (request.equals("8")) {
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
						Registration registration = new Registration(this.socket);
						registration.registerCustomer();
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
