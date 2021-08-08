package server.support;

import java.io.PrintWriter;

public class InvalidLoanException extends RuntimeException{
	public InvalidLoanException(int loanID, PrintWriter out){
		out.printf("The loan " + loanID + " does not exist.");
	}
}
