package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Registration {
	// Customer registration

	private BufferedReader in;
	private PrintWriter out;

	public Registration(Socket socket) throws IOException {
		in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);
	}

	private String takePrefix(){
		out.println("Please enter your prefix: ");
		String prefix = InputProcessor.takeValidInput("letters", in, out);
		return prefix;
	}

	private String takeFirstName(){
		out.println("Please enter your first name: ");
		String firstName = InputProcessor.takeValidInput("letters", in, out);
		return firstName;
	}

	private String takeLastName(){
		out.println("Please enter your last name: ");
		String lastName = InputProcessor.takeValidInput("letters", in, out);
		return lastName;
	}

	private String takeNationalInsuranceNumber(){
		out.println("Please enter your National Insurance Number: ");
		String nationalInsuranceNumber = InputProcessor.takeValidInput("numbers", in, out);
		// TODO: update InputProcessor to include both letters AND numbers
		return nationalInsuranceNumber;
	}

	private String takeDateOfBirth(){
		out.println("Please enter your date of birth (DD/MM/YYYY): ");
		String dateOfBirth = InputProcessor.takeValidInput("date", in, out);
		return dateOfBirth;
	}

	private Address takeAddress(){

		//TODO: tidy this up
		String number = "";
		String postcode = "";

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
		try {
			postcode = in.readLine(); // TODO: input processor for postcodes / zip codes?
		} catch(IOException e){
			e.printStackTrace();
		}

		out.println("Please enter your country: ");
		String country = InputProcessor.takeValidInput("letters", in, out);

		return new Address(number, firstLine, secondLine, city, region, postcode, country);
	}

	private String takeEmail(){
		out.println("Please enter your email address: ");
		String email = InputProcessor.takeValidInput("email", in, out);
		return email;
	}

	private String takePhoneNum(){
		out.println("Please enter your phone number (must start with a 0 followed by 10 digits): ");
		String phoneNum = InputProcessor.takeValidInput("phonenumber", in, out);
		return phoneNum;
	}

	private Password setUserCredentials(){
		try {
			out.println("Please enter a new login ID: ");
			String loginID = in.readLine();  //TODO: validate user login in input processor
			out.println("Please enter a password: ");
			String password = in.readLine();  //TODO: validate user password in input processor
			return new Password(loginID, password); // Encrypt password and store in database
		} catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}

	public Customer registerCustomer(){
		return new Customer(1, takePrefix(), takeFirstName(), takeLastName(), takeNationalInsuranceNumber(),
				takeDateOfBirth(), takeEmail(), takePhoneNum(), takeAddress(),  setUserCredentials());
	}

}
