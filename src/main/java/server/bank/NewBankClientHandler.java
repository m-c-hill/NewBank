package server.bank;

import server.database.DbUtils;
import server.database.GetObject;
import server.support.InputProcessor;
import server.user.Admin;
import server.user.Customer;
import server.user.Password;
import server.user.Registration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static server.database.Connection.getDBConnection;

/**
 * Class to handle application menus and login system
 */
public class NewBankClientHandler extends Thread {

	private final NewBank bank;
	private final BufferedReader in;
	private final PrintWriter out;
	private final Socket socket;

	private final static java.sql.Connection con = getDBConnection();

	public NewBankClientHandler(Socket socket) throws IOException {
		this.socket = socket;
		bank = NewBank.getBank();
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	/**
	 * Method to authenticate login details entered by the user and return an array of authentication information
	 * including the user id, whether the password was correct and if the account is a customer or admin account.
	 * @return Boolean array [userId, login_authenticated, isCustomer, isAdmin]
	 */
	private Object[] login() {

		String login = "";
		String password = "";
		int userId = 0;

		boolean isCustomer = false;
		boolean isAdmin = false;

		boolean validLogin = false;
		boolean grantAccess = false;

		try {
			while (!validLogin) {
				out.println("Please enter your login ID: ");
				login = in.readLine();
				out.println("Please wait...");
				if (!DbUtils.checkLoginExists(login)) {
					out.println("This login is invalid, please try again.");
				} else {
					userId = Password.getExistingUserId(login);
					validLogin = true;
				}
			}

			int count = 0;
			while (count < 3) {
				out.println("Please enter your password: ");
				password = in.readLine();
				out.println("Please wait...");
				Password credentials = new Password(login, password);
				grantAccess = credentials.authenticate(password);

				// TODO: the logic of users entering an incorrect password a maximum number of times will need thinking through a bit more
				if (!grantAccess) {
					out.println("Incorrect password. " + (3 - count) + " attempts left");
					count++;
				} else {
					break;
				}
			}

			isCustomer = isCustomer(userId);
			isAdmin = isAdmin(userId);

		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return new Object[]{userId, grantAccess, isCustomer, isAdmin};
	}

	/**
	 * Method to print New Bank's logo on start up
	 */
	private void printNewBankLogo() {
		String logo =
			"//============================================\\\\\n" +
			" | \\ | |             |  _ \\            | |   \n" +
			" |  \\| | _____      _| |_) | __ _ _ __ | | __\n" +
			" | . ` |/ _ \\ \\ /\\ / /  _ < / _` | '_ \\| |/ /\n" +
			" | |\\  |  __/\\ V  V /| |_) | (_| | | | |   <\n" +
			" |_| \\_|\\___| \\_/\\_/ |____/ \\__,_|_| |_|_|\\_\\\n" +
			"//============================================\\\\\n";
		out.println(logo);
	}

	public void run() {
		// keep getting requests from the client and processing them
		printNewBankLogo();
		try {
			// This loop will ensure that the user will always have the option to exit back to the welcome screen
			// User should execute "MENU" command
			while (true) {
				// A welcome screen offering options to login, register and recover your account
				out.println("Please choose an option:\n" +
						"1. Login as Customer\n" +
						"2. Register for a New Customer Account\n" +
						"3. Login as Admin\n" +
						"4. Recover Account\n"
				);
				switch (in.readLine()) {
					case "1":
						// Customer login option
						Object[] authCustomer = login();

						if ((boolean)authCustomer[1]) {
							out.println("Login successful");
							out.println("Loading...");
							if ((boolean)authCustomer[2]) {
								customerMenu((int)authCustomer[0]);
							} else {
								out.println("You do not have permission to access the customer menu.");
							}
						}
						break;

					case "2":
						// Account registration option
						out.println("Please wait...");
						Registration registration = new Registration(this.socket);
						registration.registerCustomer();
						break;

					case "3":
						// Admin login option
						Object[] authAdmin = login();
						if ((boolean)authAdmin[1]) {
							out.println("Login successful");
							if ((boolean) authAdmin[3]) {
								adminMenu((int) authAdmin[0]);
							} else {
								out.println("You do not have permission to access the admin menu.");
							}
						}
						break;

					case "4":
						// Account recovery option
						accountRecoveryMenu();
						break;
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

	/**
	 * Method to display the options available in the main customer menu
	 * @param userId User ID
	 */
	private void customerMenu(int userId) {

		Customer customer = GetObject.getCustomer(userId);

		while (true) {
			String request = "";
			out.println("\nPlease choose an option:"
					+ "\n1. Accounts"
					+ "\n2. Statements"
					+ "\n3. Loans"
					+ "\n4. Ethereum wallet"
					+ "\n5. Reset my password"
					+ "\n6. Back to main menu");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

			if(request.equals("6")){
				break;
			}

			switch(request){
				case "1":
					out.println(accountsMenu(customer));
					break;
				case "2":
					out.println(statementMenu(customer));
					break;
				case "3":
					out.println(loanMenu(customer));
					break;
				case "4":
					out.println(ethereumMenu(customer));
					break;
				case "5":
					try {
						assert customer != null;
						out.println(bank.resetPassword(customer, in, out));
					} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e) {
						e.printStackTrace();
					}
					break;
			}
		}
	}

	/**
	 * Method to display the options available in the admin menu
	 * @param userId User ID
	 */
	private void adminMenu(int userId) {
		Admin admin = GetObject.getAdmin(userId);

		while (true) {
			String request = "";
			out.println("\nPlease choose an option:"
					+ "\n1. View active loans"
					+ "\n2. Manage loan requests"
					+ "\n3. Back to main menu");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("3")) {
				break;
			}
			out.println(bank.processAdminRequest(admin, request, in, out));
		}
	}

	/**
	 * Method to display the options available in the customer accounts submenu
	 * @param customer Customer
	 * @return Response
	 */
	private String accountsMenu(Customer customer) {

		while (true) {
			String request = "";
			out.println("\n Accounts Menu"
					+ "\n+-----------------------+"
					+ "\n1. Show my accounts"
					+ "\n2. Withdraw amount"
					+ "\n3. Deposit amount"
					+ "\n4. Create a new account"
					+ "\n5. Close an account"
					+ "\n6. Back");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("6")){
				return "Returning to customer menu";
			}
			return bank.processCustomerAccountRequest(customer, request, in, out);
		}
	}

	/**
	 * Method to display the options available in the customer loans submenu
	 * @param customer Customer
	 * @return Response
	 */
	private String loanMenu(Customer customer) {

		while (true) {
			String request = "";
			out.println("\n Loans Menu"
					+ "\n+-----------------------+"
					+ "\n1. Request a loan"
					+ "\n2. View my loan status"
					+ "\n3. Make loan payment"
					+ "\n4. Back");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("3")){
				return "Returning to customer menu";
			}
			return bank.processCustomerLoanRequest(customer, request, in, out);
		}
	}

	/**
	 * Method to display the options available in the customer ethereum wallet submenu
	 * @param customer Customer
	 * @return Response
	 */
	private String statementMenu(Customer customer) {

		while (true) {
			String request = "";
			out.println("\n Statements Menu"
					+ "\n+-----------------------+"
					+ "\n1. View my recent transactions"
					+ "\n2. Request statement email"
					+ "\n3. Back");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("3")){
				return "Returning to customer menu";
			}
			return bank.processCustomerStatementRequest(customer, request, in, out);
		}
	}

	/**
	 * Method to display the options available in the customer statements submenu
	 * @param customer Customer
	 * @return Response
	 */
	private String ethereumMenu(Customer customer) {
		while (true) {
			String request = "";
			out.println("\n My Ethereum Wallet"
					+ "\n+-----------------------+"
					+ "\n1. Create Ethereum wallet"
					+ "\n2. Show Ethereum wallet"
					+ "\n3. Transfer Ether"
					+ "\n4. Back");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("4")){
				return "Returning to customer menu";
			}
			return bank.processCustomerEthereumRequest(customer, request, in, out);
		}
	}

	/**
	 * Method to determine if a user is a customer
	 * @param userId User ID
	 * @return True if user is a customer
	 */
	private boolean isCustomer(int userId) {
		String query = "SELECT 1 FROM customer WHERE user_id = ?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method to determine if a user is an admin
	 * @param userId User ID
	 * @return True if user is a customer
	 */
	private boolean isAdmin(int userId){
		String query = "SELECT 1 FROM admin WHERE user_id = ?";
		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setInt(1, userId);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.isBeforeFirst() ){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Method to display the account recovery menu
	 */
	private void accountRecoveryMenu() {
		out.println("Please choose an option:\n" +
				"1. Forgotten account login\n" +
				"2. Forgotten account password\n" +
				"3. Go back");
		try {
			switch (in.readLine()) {
				case "1":
					forgottenLogin();
					break;
				case "2":
					try {
						forgottenPassword();
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						e.printStackTrace();
					}
				case "3":
					break;
				default:
					out.println("Invalid input, please try again: ");
					accountRecoveryMenu();
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Method to retrieve a user's forgotten login ID
	 */
	private void forgottenLogin() {
		int userId = verifyUserIdentity();
		if (userId == -1) {
			out.println("The details you have entered do not match any user in our system.");
		} else {
			out.println("Thank you for verifying your identity.\n");
			out.println("Your user id is: " + Password.getUserLogin(userId));
		}
	}

	/**
	 * Method to reset the password for a user who has forgotten theirs
	 */
	private void forgottenPassword() throws NoSuchAlgorithmException, InvalidKeySpecException {

		out.println("Please enter your Login ID: ");
		String loginId = InputProcessor.takeValidInput("letters and numbers", in, out);
		if(!DbUtils.checkLoginExists(loginId)){
			out.println("This login does not exist. Exiting to main menu.");
			return;
		}
		int userId = verifyUserIdentity();
		if (userId == -1) {
			out.println("The details you have entered do not match any user in our system.");
		} else {
			out.println("Please enter your new password: ");
			String passwordAttempt1 = InputProcessor.takeValidInput("password", in, out);
			out.println("Please re-enter your new password");
			String passwordAttempt2 = InputProcessor.takeValidInput("password", in, out);

			if (passwordAttempt1.equals(passwordAttempt2)){
				Password password = new Password(userId, Password.getUserLogin(userId));
				password.resetPassword(passwordAttempt1);
			}
		}
	}

	/**
	 * Method to verify a user's identity, to be used before retrieving a forgotten login or resetting a password
	 * @return User ID if user's identity can be verified through a series of questions (else return -1)
	 */
	private int verifyUserIdentity() {
		// This could be made more secure by asking for national insurance number and recent transactions, but this
		// is good enough for now as a proof of concept

		out.println(
				"To recover your account, we'll need to verify your identity.\n" +
				"Please enter the following details: "
		);

		try{
			out.println("First name: ");
			String firstName = in.readLine();
			out.println("Last name: ");
			String lastName = in.readLine();
			out.println("Postcode (format EN8 9HG): ");
			String postcode = in.readLine();
			out.println("Date of Birth (format YYYY-MM-DD): ");
			String dateOfBirth = in.readLine();
			out.println("Checking your details...");
			return DbUtils.accountRecovery(firstName, lastName, dateOfBirth, postcode);

		} catch(IOException e){
			e.printStackTrace();
		}
		return -1;
	}
}
