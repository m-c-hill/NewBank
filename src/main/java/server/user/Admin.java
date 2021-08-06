package server.user;

import server.account.Account;
import server.bank.Address;
import server.bank.BankLoan;
import server.database.GetObject;
import server.support.InputProcessor;
import server.support.InvalidLoanException;
import server.support.OutputProcessor;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends User {
	AdminRole role;

	public Admin(int userID, String prefix, String fName, String lName, String nationalInsuranceNumber,
				 String dateOfBirth, String emailAddress, String phoneNumber, Address address, AdminRole role) {
		super(userID, prefix, fName, lName, nationalInsuranceNumber, dateOfBirth, emailAddress, phoneNumber, address);
		this.role = role;
	}

	/**
	 * Method to check the permissions of a given admin (ie. can view user information and grant loans)
	 * @param taskName Name of task to check permissions for
	 * @return True if admin has permission to execute task
	 */
	private boolean checkPermission(String taskName){
		// Method to check if an admin has the required permissions perform a specific task
		switch(taskName){
			case "viewUserInfo":
				return role.canViewUserInfo();
			case "viewUserStatement":
				return role.canViewUserStatement();
			case "openAccount":
				return role.canOpenAccount();
			case "closeAccount":
				return role.canCloseAccount();
			case "grantLoan":
				return role.canHandleLoanRequests();
            case "viewLoans":
                return role.canViewLoanRequests();
			default:
				System.out.println("Warning: you do not have permission to perform the following action: " + taskName);
				return false;
		}
	}

	/**
	 * Method to handle loan requests
	 * @param in Input
	 * @param out Output
	 * @return Response
	 */
    public String handleLoanRequest(BufferedReader in, PrintWriter out) {
        if (this.checkPermission("grantLoan")) {
			// Display only the loans that are pending
			ArrayList<BankLoan> pendingLoansList = GetObject.getPendingLoanList();
			if (!pendingLoansList.isEmpty()) {
				out.println(OutputProcessor.createPendingLoansTable());

				out.println("Enter the ID of the loan you wish to handle a request for: ");

				boolean loanSelected = false;
				while(!loanSelected) {
					int loanId = InputProcessor.takeValidIntegerInput(in, out);
					try {
						BankLoan bankLoan = selectLoanRequest(loanId, pendingLoansList);
					} catch (InvalidLoanException e){
						out.println("The loan ID you have requested is invalid, please enter a valid loan id: ");
					}
				}

				// Choose whether to accept or reject
				out.println(
						"Please enter ACCEPT to accept or REJECT to reject the selected loan: ");
				String decision = InputProcessor.takeValidLoanDecisionInput(in, out);

				// Handle either decision
				if (decision.equalsIgnoreCase("ACCEPT")) {
					this.acceptLoanRequest(bankLoan);
					return "Process completed successfully. Loan request was accepted.";
				} else {
					this.rejectLoanRequest(bankLoan);
					return "Process completed successfully. Loan request was rejected.";
				}
			} else {
				return "There are no pending requests at the moment.";
			}
		}
		return "You're not authorized to perform this action";
    }

	/**
	 * Method to select a valid loan request
	 * @param loanId Loan ID
	 * @param pendingLoansList Pending list of Bank Loans
	 * @return Selected Bank Loan
	 */
    private BankLoan selectLoanRequest(int loanId, ArrayList<BankLoan> pendingLoansList){

	}

	/**
	 * Method to accept loan requests and trigger the account method to transfer the loan to the customre
	 * @param bankLoan Bank Loan
	 */
	private void acceptLoanRequest(BankLoan bankLoan){
        // Add the loan amount to the account balance
        Account customerAccount = bankLoan.getAccount();

        // Change the loan request status in the loans list
        bankLoan.updateApprovalStatus("approved");

        // Transfer the loan to the user's account
		customerAccount.receiveLoan(bankLoan.getAmountLoaned());
        bankLoan.updateTransferStatus(true);
    }

	/**
	 * Method to reject a loan
	 * @param bankLoan Bank Loan
	 */
	private void rejectLoanRequest(BankLoan bankLoan){
		bankLoan.updateApprovalStatus("declined");
        bankLoan.getCustomer().setAllowedToRequestLoan(true);
    }

	/**
	 * Method to display the loans table
	 * @param loansList Loans List
	 * @param out Output
	 * @return Response
	 */
    public String showLoansList(ArrayList<BankLoan> loansList, PrintWriter out) {
        if (this.checkPermission("viewLoans")) {
            out.println(OutputProcessor.createLoansTable(loansList));
        } else {
            out.println("You're not authorized to access the loans list.");
        }
        return "Success";
    }

	private void viewUserInfo(int userID){
		//TODO: create method to allow admin to view user information
		if(checkPermission("viewUserInfo")) {
			//code here
		}
	}

	private void viewUserStatement(int userID){
		//TODO: create method to allow admin to view recent statements for a given user / account
		if(checkPermission("viewUserStatement")) {
			// code here
		}
	}

	private void openAccount(){
		// TODO: create method to open an account
		if(checkPermission("openAccount")) {
			// code here
		}
	}

	private void closeAccount(String accountNumber){
		if(checkPermission("closeAccount")) {
			Account accountToClose = GetObject.getAccount(accountNumber);
			accountToClose.closeAccount();
		}
	}

}
