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

	public Registration(Socket s) throws IOException {
		in = new BufferedReader(new InputStreamReader(s.getInputStream()));
		out = new PrintWriter(s.getOutputStream(), true);
	}

	private String prefix(){
		out.println("Please enter your prefix:");
		String prefix = InputProcessor.takeValidInput("letters", in, out);
		return prefix;
	}

	private String takeFirstName(){
		out.println("Please enter your first name:");
		String firstName = InputProcessor.takeValidInput("letters", in, out);
		return firstName;
	}

	private String takeLastName(){
		out.println("Please enter your last name:");
		String lastName = InputProcessor.takeValidInput("letters", in, out);
		return lastName;
	}

	private String

	private String takeNationalInsuranceNumber(){
		out.println("Please enter your National Insurance Number:");
		String nationalInsuranceNumber = InputProcessor.takeValidInput("numbers", in, out);
		return nationalInsuranceNumber;
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

	private Address takeAddress(){
		out.println("Please enter your address number: ");
		String

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


}
