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

    public void showLoansList(ArrayList<BankLoan> loansList, PrintWriter out) {
        if (this.adminRoles.isAllowedToViewLoanList()) {
            out.println("Customer Name:\2tLoan Reason:\2tIs Accepted?\2tIs Paid Back?");
            for (BankLoan bankLoan : loansList) {
                out.print(bankLoan.getCustomer() + "\2t" 
                + bankLoan.getReason() + "\2t" 
                + bankLoan.isAccepted()+ "\2t" 
                + bankLoan.isPaidBack());
            }
        } else {
            out.println("You're not authorized to access the loans list.");
        }
    }

}
