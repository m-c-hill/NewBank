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
	private UserCredentials takeCredentials(BufferedReader in, PrintWriter out) throws IOException{
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

	public void run() {
		// keep getting requests from the client and processing them
		try {
			// A welcome screen offering one option to login and another to register
			out.println("Please choose an option:\n1. Login\n2. Register");
			switch (in.readLine()) {
				case "1":
					UserCredentials uc = takeCredentials(in, out);
					CustomerID customer = bank.checkLogInDetails(uc.getUsername(), uc.getPassword());
					// if the user is authenticated then get requests from the user and process them
					if (customer != null) {
						out.println("Log In Successful. What do you want to do?");
						while (true) {
							String request = in.readLine();
							System.out.println("Request from " + customer.getKey());
							String responce = bank.processRequest(customer, request, in, out);
							out.println(responce);
						}
					} else {
						out.println("Log In Failed");
					}
					break;

				// You can use this for account creation? (Register)	
				case "2":
					break;
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
