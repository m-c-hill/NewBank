package server;

import java.io.PrintWriter;
import java.util.ArrayList;

public class OutputProcessor {

    // Loan table format with spacing for every column
    private static final String loanTableContentFormat = "|%-15s|%-35s|%-6s|%-6s|%-9s|%-9s|%-9s|%n";
    private static final String loanTableHeader = String.format(
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n" +
        "| Customer Name |              Reason               |Amount| ATPB |Checked? |Accepted?|PaidBack?|%n" +
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n");
    private static final String loanTableRowSeparator = String.format(
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n");

    // Accounts table format
    private static final String accountsTableContentFormat = "|%-12s|%-10s|%-8s|%n";
    private static final String accountsTableHeader = String.format(
        "+------------+----------+--------+%n" +
        "|Account Name|Balanace  |Currency|%n" +
        "+------------+----------+--------+%n"
    );
    private static final String accountsTableRowSeparator = String.format(
        "+------------+----------+--------+%n");
    
    // Loans table creator method for admins
    public static String createLoansTable(ArrayList<BankLoan> loansList) {
        String loansTable = loanTableHeader;

        for (BankLoan bankLoan : loansList) {
            loansTable = loansTable + String.format(loanTableContentFormat,
                    bankLoan.getCustomer().getFirstName() + " " + bankLoan.getCustomer().getLastName(),
                    bankLoan.getReason(), Double.toString(bankLoan.getAmount()),
                    Double.toString(bankLoan.getPayBackAmount()), Boolean.toString(bankLoan.isChecked()),
                    Boolean.toString(bankLoan.isAccepted()), Boolean.toString(bankLoan.isPaidBack()));
            loansTable = loansTable + loanTableRowSeparator;
        }

        return loansTable;
    }

    // Accounts table creator method
    public static String createsAccountsTable(ArrayList<Account> accountsList){
        String accountsTable = accountsTableHeader;

        for (Account account : accountsList) {
            accountsTable = accountsTable + String.format(accountsTableContentFormat, 
                    account.getAccountNumber(),
                    Double.toString(account.getPrimaryBalance().getBalance()),
                    account.getPrimaryBalance().getCurrency().toUpperCase());
            accountsTable = accountsTable + accountsTableRowSeparator;
        }

        return accountsTable;
    }

}