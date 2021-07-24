package server;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends User {

    private AdminRoles adminRoles;

    public Admin(String fName, String lName, String ssn, String dob, String pob, String email, String phoneNum,
            String address, AdminRoles adminRoles) {
        super(fName, lName, ssn, dob, pob, email, phoneNum, address);
        this.adminRoles = adminRoles;
    }

    // Showing the loans list
    // This contains all the loan requests, accepted, rejected, paid back, and non-paid back
    public void showLoansList(ArrayList<BankLoan> loansList, PrintWriter out) {
        if (this.adminRoles.isAllowedToViewLoanList()) {
            this.createTable(loansList, out);
        } else {
            out.println("You're not authorized to access the loans list.");
        }
    }

    // Accepting or rejecting a loan request
    public String handleLoanRequest(ArrayList<BankLoan> loansList, HashMap<String, Customer> customers, BufferedReader in, PrintWriter out){
        if (this.adminRoles.isAllowedToGrantLoan()) {
            // Create the pending loans list
            ArrayList<BankLoan> pendingLoansList = this.createPendingLoansList(loansList);
            if (!pendingLoansList.isEmpty()) {
                this.createTable(pendingLoansList, out);
            } else {
                out.println("There are no pending requests at the moment.");
            }

            // Validating the name (Customer firstName)
            out.println("Enter the name of the customer you wish to handle a request for:");
            String customerName = InputProcessor.takeValidCustomerNameInput(pendingLoansList, in, out);

            // Choose whether to accept or reject
            out.println("Please enter ACCEPT to accept or REJECT to reject the loan request (this is case insensitive):");
            String decision = InputProcessor.takeValidLoanDecisionInput(in, out);

            // Handle either decision
            if(decision.equalsIgnoreCase("ACCEPT")){
                this.acceptLoanRequest(customerName, customers, loansList);
            }

        }
        else{
            return "You're not authorized to perform this action";
        }
        return "Going back to the main menu.";
    }

    // Pending loans list creator method
    private ArrayList<BankLoan> createPendingLoansList(ArrayList<BankLoan> loansList) {
        ArrayList<BankLoan> pendingLoansList = new ArrayList<>();

        for (BankLoan bankLoan : loansList) {
            if (!bankLoan.isAccepted()) {
                pendingLoansList.add(bankLoan);
            }
        }

        return pendingLoansList;
    }

    // Loan request acceptor method
    private void acceptLoanRequest(String customerName, HashMap<String, Customer> customers, ArrayList<BankLoan> loansList){
        
    }

    // Table creator method
    public void createTable(ArrayList<BankLoan> loansList, PrintWriter out){
        String tableFormat = "|%-15s|%-35s|%-6s|%-6s|%-9s|%-9s|%n";
            out.format("+---------------+-----------------------------------+------+------+---------+---------+%n");
            out.format("| Customer Name |              Reason               |Amount| ATPB |Accepted?|PaidBack?|%n");
            out.format("+---------------+-----------------------------------+------+------+---------+---------+%n");
            
            for (BankLoan bankLoan : loansList) {
                out.format(tableFormat, 
                bankLoan.getCustomer().getFirstName() + " " + bankLoan.getCustomer().getLastName(), 
                bankLoan.getReason(),
                Double.toString(bankLoan.getAmount()),
                Double.toString(bankLoan.getPayBackAmount()),
                Boolean.toString(bankLoan.isAccepted()),
                Boolean.toString(bankLoan.isPaidBack()));
            }
            out.format("+---------------+-----------------------------------+------+------+---------+---------+%n");
    }
}
