package server;

import java.io.PrintWriter;
import java.util.ArrayList;

public class TableCreator {

    // Table format with spacing for every column
    private static final String loanTableContentFormat = "|%-15s|%-35s|%-6s|%-6s|%-9s|%-9s|%-9s|%n";

    private static final String loanTableHeader = String.format(
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n" +
        "| Customer Name |              Reason               |Amount| ATPB |Checked? |Accepted?|PaidBack?|%n" +
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n");

    private static final String loanTableRowSeparator = String.format(
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n");
    // Table creator method for admins
    public static void createLoansTable(ArrayList<BankLoan> loansList, PrintWriter out) {
        out.print(loanTableHeader);

        for (BankLoan bankLoan : loansList) {
            out.format(loanTableContentFormat,
                    bankLoan.getCustomer().getFirstName() + " " + bankLoan.getCustomer().getLastName(),
                    bankLoan.getReason(), Double.toString(bankLoan.getAmount()),
                    Double.toString(bankLoan.getPayBackAmount()), Boolean.toString(bankLoan.isChecked()),
                    Boolean.toString(bankLoan.isAccepted()), Boolean.toString(bankLoan.isPaidBack()));
            out.print(loanTableRowSeparator);
        }
    }

    private static void createMyLoanStatusTable(){
        
    }

    // Table creator method for showing a single loan belonging to the current Customer object

}