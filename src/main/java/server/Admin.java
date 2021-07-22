package server;

import java.io.PrintWriter;
import java.util.ArrayList;

public class Admin extends User {

    private AdminRoles adminRoles;

    public Admin(String fName, String lName, String ssn, String dob, String pob, String email, String phoneNum,
            String address, AdminRoles adminRoles) {
        super(fName, lName, ssn, dob, pob, email, phoneNum, address);
        this.adminRoles = adminRoles;
    }

    // Showing the loans list
    public void showLoansList(ArrayList<BankLoan> loansList, PrintWriter out) {
        if (this.adminRoles.isAllowedToViewLoanList()) {
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
        } else {
            out.println("You're not authorized to access the loans list.");
        }
    }

    // Approving a loan request

    // Declining a loan request

}
