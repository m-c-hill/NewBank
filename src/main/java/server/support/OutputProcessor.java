package server.support;

import server.account.Account;
import server.bank.BankLoan;

import java.util.ArrayList;

public class OutputProcessor {

    // Loan table format for admins
    private static final String LOAN_TABLE_CONTENT_FORMAT = "|%-21s|%-18s|%-32s|%-15s|%-32s|%n";
    private static final String LOAN_TABLE_HEADER = String.format(
        "+---------------------+------------------+--------------------------------+---------------+--------------------------------+%n" +
        "|   Account Number    | Requested Amount |             Reason             | Interest Rate |             Status             |%n" +
        "+---------------------+------------------+--------------------------------+---------------+--------------------------------+%n");
    private static final String LOAN_TABLE_ROW_SEPARATOR = String.format(
        "+---------------------+------------------+--------------------------------+---------------+--------------------------------+%n");

    // Accounts table format
    private static final String ACCOUNT_TABLE_CONTENT_FORMAT = "|%-15s|%-10s|%-8s|%n";
    private static final String ACCOUNTS_TABLE_HEADER = String.format(
        "+---------------+----------+--------+%n" +
        "|Account Number |Balanace  |Currency|%n" +
        "+---------------+----------+--------+%n"
    );
    private static final String ACCOUNTS_TABLE_ROW_SEPARATOR = String.format(
        "+---------------+----------+--------+%n");

    // Loans table for Admins
    public static String createLoansTable(ArrayList<BankLoan> loansList) {
        String loansTable = LOAN_TABLE_HEADER;

        for (BankLoan bankLoan : loansList) {
            loansTable += String.format(LOAN_TABLE_CONTENT_FORMAT,
                                        bankLoan.getAccount().getAccountNumber(),
                                        bankLoan.getAmountLoaned(),
                                        bankLoan.getReason(),
                                        bankLoan.getInterestRate(),
                                        bankLoan.getStatus());
            loansTable += LOAN_TABLE_ROW_SEPARATOR;
        }

        return loansTable;
    }

    // Accounts table creator method
    public static String createsAccountsTable(ArrayList<Account> accountsList){
        String accountsTable = ACCOUNTS_TABLE_HEADER;

        for (Account account : accountsList) {
            accountsTable = accountsTable + String.format(ACCOUNT_TABLE_CONTENT_FORMAT,
                    account.getAccountNumber(),
                    account.getBalance(),
                    account.getCurrency().getCurrencyId().toUpperCase());
            accountsTable = accountsTable + ACCOUNTS_TABLE_ROW_SEPARATOR;
        }

        return accountsTable;
    }

}