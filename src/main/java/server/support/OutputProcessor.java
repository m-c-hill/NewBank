package server.support;

import server.account.Account;
import server.bank.BankLoan;
import server.transaction.Transaction;

import java.util.ArrayList;
import java.util.Locale;

public class OutputProcessor {

    // Loan table format
    private static final String LOAN_TABLE_CONTENT_FORMAT = "|%-15s|%-35s|%-6s|%-6s|%-9s|%-9s|%-9s|%n";
    private static final String LOAN_TABLE_HEADER =
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n" +
        "| Customer Name |              Reason               |Amount| ATPB |Checked? |Accepted?|PaidBack?|%n" +
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n";
    private static final String LOAN_TABLE_ROW_SEPARATOR =
        "+---------------+-----------------------------------+------+------+---------+---------+---------+%n";

    // Accounts table format
    private static final String ACCOUNTS_TABLE_CONTENT_FORMAT = "|%-15s|%-10s|%-8s|%n";
    private static final String ACCOUNTS_TABLE_HEADER =
        "+---------------+----------+--------+%n" +
        "|Account Number |  Balance |Currency|%n" +
        "+---------------+----------+--------+%n";
    private static final String ACCOUNTS_TABLE_ROW_SEPARATOR =
        "+---------------+----------+--------+%n";

    // Transactions table format (statements)
    private static final String TRANSACTIONS_TABLE_CONTENT_FORMAT = "|%-13s|%-20s|%-13s|%-10s|%n";
    private static final String TRANSACTIONS_TABLE_HEADER =
            "+-------------+--------------------+-------------+----------+%n"+
            "|     Date    |        Payee       |    Amount   | Currency |%n"+
            "+-------------+--------------------+-------------+----------+%n";
    private static final String TRANSACTIONS_TABLE_ROW_SEPARATOR =
            "+-------------+--------------------+-------------+----------+%n";

    // Loans table creator
    public static String createLoansTable(ArrayList<BankLoan> loansList) {
        String loansTable = LOAN_TABLE_HEADER;

        for (BankLoan bankLoan : loansList) {
            loansTable += String.format(LOAN_TABLE_CONTENT_FORMAT,
                    bankLoan.getCustomer().getFirstName() + " " + bankLoan.getCustomer().getLastName(),
                    bankLoan.getReason(), bankLoan.getAmount(),
                    bankLoan.getPayBackAmount(), bankLoan.isChecked(),
                    bankLoan.isAccepted(), bankLoan.isPaidBack());
            loansTable += LOAN_TABLE_ROW_SEPARATOR;
        }

        return loansTable;
    }

    // Accounts table creator
    public static String createsAccountsTable(ArrayList<Account> accountsList) {
        String accountsTable = ACCOUNTS_TABLE_HEADER;

        for (Account account : accountsList) {
            accountsTable += String.format(ACCOUNTS_TABLE_CONTENT_FORMAT,
                    account.getAccountNumber(),
                    account.getBalance(),
                    account.getCurrency().getName().toUpperCase());
            accountsTable += ACCOUNTS_TABLE_ROW_SEPARATOR;
        }

        return accountsTable;
    }

    // Transactions table creator
    public static String createTransactionsTable(ArrayList<Transaction> transactions) {
        String transactionsTable = TRANSACTIONS_TABLE_HEADER;

        for(Transaction transaction: transactions){
            transactionsTable += String.format(TRANSACTIONS_TABLE_CONTENT_FORMAT,
                    transaction.getTimestamp().toString(),
                    transaction.getPayee(),
                    transaction.getAmount(),
                    transaction.getCurrency().getName().toUpperCase());
            transactionsTable += TRANSACTIONS_TABLE_ROW_SEPARATOR;
        }

        return transactionsTable;
    }

}