package server.user;

import server.bank.Address;
import server.database.Connection;
import server.database.DbUtils;
import server.support.InputProcessor;

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

public class Registration {
	// Customer registration

	private final BufferedReader in;
	private final PrintWriter out;
	private static java.sql.Connection con = getDBConnection();

	public Registration(Socket socket) throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	private String takePrefix(){
		out.println("Please enter your prefix: ");
		return InputProcessor.takeValidInput("letters", in, out);
	}

	private String takeFirstName(){
		out.println("Please enter your first name: ");
		return InputProcessor.takeValidInput("letters", in, out);
	}

	private String takeLastName(){
		out.println("Please enter your last name: ");
		return InputProcessor.takeValidInput("letters", in, out);
	}

	private String takeNationalInsuranceNumber(){
		out.println("Please enter your National Insurance Number: ");
		// TODO: update InputProcessor to include both letters AND numbers
		return InputProcessor.takeValidInput("numbers", in, out);
	}

	private String takeDateOfBirth(){
		out.println("Please enter your date of birth (DD/MM/YYYY): ");
		return InputProcessor.takeValidInput("date", in, out);
	}

	private Address takeAddress(){

		//TODO: tidy this up
		String number = "";

		out.println("Please enter your address number: ");
		try {
			number = in.readLine(); // TODO: both letters and numbers acceptable, update InputProcessor?
		} catch (IOException e){
			e.printStackTrace();
		}

		out.println("Please enter your first address line: ");
		String firstLine = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your second address line: ");
		String secondLine = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your city: ");
		String city = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your county/state: ");
		String region = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your postcode: ");
		String postcode = InputProcessor.takeValidInput("postcode", in, out);

		out.println("Please enter your country: ");
		String country = InputProcessor.takeValidInput("letters", in, out);

		return new Address(number, firstLine, secondLine, city, region, postcode, country);
	}

	private String takeEmail(){
		out.println("Please enter your email address: ");
		return InputProcessor.takeValidInput("email", in, out);
	}

	private String takePhoneNum(){
		out.println("Please enter your phone number (must start with a 0 followed by 10 digits): ");
		return InputProcessor.takeValidInput("phonenumber", in, out);
	}

	/**
	 * Method to take a new login ID and password from the user.
	 * Password is encrypted and stored using a basic salt and hash method.
	 */
	private void setUserCredentials() {

		int newUserId = getNewUserId();
		String loginId = "";
		String plainTextPassword = "";

		boolean loginValid = false;
		try {
			while(!loginValid) {
				out.println("Please enter a new login ID: ");
				loginId = in.readLine();  //TODO: validate user login in input processor (ie. no spaces, invalid characters)
				if (checkLoginExists(loginId)) {
					out.println("This login has already been taken, please try again.");
				}
				else {
					loginValid = true;
				}
			}
			out.println("Please enter a password: ");
			plainTextPassword = in.readLine();  //TODO: validate user password in input processor
			Password password = new Password(newUserId, loginId, plainTextPassword);
			out.println("Password successfully encrypted and stored.");
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
			e.printStackTrace();
		}
	}


	/**
	 * Method to register a new customer
	 * @return
	 */
	public boolean registerCustomer(){

		Customer newCustomer = new Customer(0, takePrefix(), takeFirstName(), takeLastName(), takeNationalInsuranceNumber(),
				takeDateOfBirth(), takeEmail(), takePhoneNum(), takeAddress());

		// Set and store user's login/hash separately
		setUserCredentials();

		try {
			DbUtils utils = new DbUtils(out);
			utils.registerNewCustomer(newCustomer);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	/**
	 * Method to return the next user ID to be stored in the database
	 * @return User ID
	 */
	private int getNewUserId(){
		int userId = -1;
		String query = "SELECT MAX(user_id) FROM user";

		try {
			PreparedStatement preparedStatement = con.prepareStatement(query);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()) {
				userId = rs.getInt("user_id") + 1;
			}
		} catch(SQLException e){
			e.printStackTrace();
		}
		return userId;
	}

	/**
	 * Method to check if user has entered a unique ID (check in database)
	 * @return True if user login is
	 */
	public boolean checkLoginExists(String login){
		String query = "SELECT 1 FROM password WHERE login = ?";
		try{
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, login);
			ResultSet rs = preparedStatement.executeQuery();
			if (rs.next()){
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}


