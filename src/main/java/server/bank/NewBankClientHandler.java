package server.bank;

import server.database.DbUtils;
import server.database.GetObject;
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

public class NewBankClientHandler extends Thread {

	private NewBank bank;
	private BufferedReader in;
	private PrintWriter out;
	private Socket socket;

	private final static java.sql.Connection con = getDBConnection();

	public NewBankClientHandler(Socket socket) throws IOException {
		socket = socket;
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

	// Adding the customer object to bank.customers<String, Customer> HashMap
	private void registerCustomer(Customer c) {
		this.bank.addCustomer(c);
	}

	public void run() {
		// keep getting requests from the client and processing them
		try {
			// This loop will ensure that the user will always have the option to exit back to the welcome screen
			// User should execute "MENU" command
			while (true) {
				// A welcome screen offering one option to login and another to register
				// TODO: add account recovery method for forgotten passwords
				out.println("Please choose an option:\n1. Login as Customer\n2. Register for a New Customer Account\n3. Login as Admin");
				switch (in.readLine()) {
					case "1":
						Object[] authCust = login();

						if ((boolean)authCust[1]) {
							out.println("Login successful");
							if ((boolean)authCust[2]) {
								customerMenu((int)authCust[0]);
							} else {
								out.println("You do not have permission to access the customer menu.");
							}
						}
						break;

					case "2":
						Registration registration = new Registration(this.socket);
						registration.registerCustomer();
						break;

					case "3":
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
	 * Method to display the options available in the customer menu
	 * @param userId User ID
	 */
	private void customerMenu(int userId) {

		Customer customer = GetObject.getCustomer(userId);

		while (true) {
			String request = "";
			out.println("Please choose an option:"
					+ "\n1. Show my accounts"
					+ "\n2. Withdraw amount"
					+ "\n3. Deposit amount"
					+ "\n4. Create a new account"
					+ "\n5. Request a loan"
					+ "\n6. View my loan status"
					+ "\n7. Pay back my loan"
					+ "\n8. Go back to the main menu");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("8")) {
				break;
			}
			String response = bank.processCustomerRequest(customer, request, in, out);
			out.println(response);
		}
	}

	/**
	 * Method to display the options available in the admin menu
	 * @param userId User ID
	 */
	private void adminMenu(int userId) {

		// TODO: update required to get admin objects from the database
		Admin admin = GetObject.getAdmin(userId);

		while (true) {
			String request = "";
			out.println("Please choose an option:"
					+ "\n1. Check loans list"
					+ "\n2. Accept/Decline a loan request"
					+ "\n3. Go back to the main menu");
			try {
				request = in.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (request.equals("3")) {
				break;
			}
			String response = bank.processAdminRequest(admin, request, in, out);
			out.println(response);
		}
	}

	private void recoverAccount(){
		// TODO: add account recovery method for forgotten passwords
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
			if (rs.isBeforeFirst() ){
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
}
