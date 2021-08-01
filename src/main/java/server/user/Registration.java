package server.user;

import server.bank.Address;
import server.database.DbUtils;
import server.support.InputProcessor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import static server.database.Connection.getDBConnection;

/** Class to register a user in the system (either a customer or an admin)
 */
public class Registration {
	private final BufferedReader in;
	private final PrintWriter out;
	private final static java.sql.Connection con = getDBConnection();

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
		out.println("Please enter your National Insurance/Social Security Number: ");
		String nationalInsuranceNumber = InputProcessor.takeValidInput("letters and numbers", in, out);
		return nationalInsuranceNumber;
	}

	private String takeDateOfBirth(){
		out.println("Please enter your date of birth in DDMMYYYY format: ");
		String dateOfBirth = InputProcessor.takeValidInput("valid dates", in, out);
		return dateOfBirth;
	}

	private Address takeAddress(){
		out.println("Please enter your house number and first line of your address: ");
		String firstLine = InputProcessor.takeValidInput("letters and numbers", in, out);

		out.println("Please enter your second address line: ");
		String secondLine = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your city: ");
		String city = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your county/state: ");
		String region = InputProcessor.takeValidInput("letters", in, out);

		out.println("Please enter your postcode/zipcode: ");
		String postcode = InputProcessor.takeValidInput("valid postcodes/zipcodes", in, out);

		out.println("Please enter your country: ");
		String country = InputProcessor.takeValidInput("letters", in, out);

		return new Address(firstLine, secondLine, city, region, postcode, country);
	}

	private String takeEmail(){
		out.println("Please enter your email address: ");
		String email = InputProcessor.takeValidInput("valid email addresses", in, out);
		return email;
	}

	private String takePhoneNum(){
		out.println("Please enter your phone number (no spaces): ");
		String phoneNum = InputProcessor.takeValidInput("valid phone numbers", in, out);
		return phoneNum;
	}

	/**
	 * Method to take a new login ID and password from the user.
	 * Password is encrypted and stored using a basic salt and hash method.
	 */
	private void setUserCredentials() {
		String loginId = "";
		String plainTextPassword = "";

		boolean loginValid = false;
		try {
			while(!loginValid) {
				out.println("Please enter a new login ID: ");
				loginId = in.readLine();  //TODO: validate user login in input processor (ie. no spaces, invalid characters)
				if (DbUtils.checkLoginExists(loginId)) {
					out.println("This login has already been taken, please try again.");
				}
				else {
					loginValid = true;
				}
			}
			out.println("Please enter a password: ");
			plainTextPassword = in.readLine();  //TODO: validate user password in input processor
			Password password = new Password(loginId, plainTextPassword);
			out.println("Password successfully encrypted and stored.");
		} catch (IOException | NoSuchAlgorithmException | InvalidKeySpecException e){
			e.printStackTrace();
		}
	}

	/**
	 * Method to register a new customer
	 * @return True if customer is registered successfully
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

	// TODO: fix this once admin class is fixed
	/**
	 * Method to register a new customer
	 * @return True if customer is registered successfully
	 */
	public boolean registerAdmin(){
		Admin newAdmin = new Admin(...);
		setUserCredentials();

		try {
			DbUtils utils = new DbUtils(out);
			utils.registerNewAdmin(newAdmin);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;

	}
}
