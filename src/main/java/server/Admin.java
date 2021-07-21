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
            out.println("Customer Name\2tLoan Reason\2tAmount\2tAmount to Pay Back\2tAccepted?\2tPaid Back?");
            for (BankLoan bankLoan : loansList) {
                out.print(bankLoan.getCustomer().getFirstName() + " " + bankLoan.getCustomer().getLastName() + "\2t" 
                + bankLoan.getReason() + "\2t" 
                + bankLoan.getAmount() + "\2t"
                + bankLoan.getPayBackAmount() + "\2t"
                + bankLoan.isAccepted()+ "\2t" 
                + bankLoan.isPaidBack());
            }
        } else {
            out.println("You're not authorized to access the loans list.");
        }
    }

    // Approving a loan request

    // Declining a loan request

}
