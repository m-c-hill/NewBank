package server.support;

import server.account.Account;
import server.bank.BankLoan;
import server.transaction.Transaction;

import java.util.ArrayList;

public class OutputProcessor {

    // Loan table format
    private static final String LOAN_TABLE_CONTENT_FORMAT = "|%-9s|%-22s|%-18s|%-32s|%-15s|%-32s|%n";
    private static final String LOAN_TABLE_HEADER = String.format(
        "+---------+----------------------+------------------+--------------------------------+---------------+--------------------------------+%n" +
        "| Loan ID |    Account Number    | Requested Amount |             Reason             | Interest Rate |             Status             |%n" +
        "+---------+----------------------+------------------+--------------------------------+---------------+--------------------------------+%n");
    private static final String LOAN_TABLE_ROW_SEPARATOR = String.format(
        "+---------+----------------------+------------------+--------------------------------+---------------+--------------------------------+%n");

    // Accounts table format
    private static final String ACCOUNT_TABLE_CONTENT_FORMAT = "|%-15s|%-10s|%-8s|%n";
    private static final String ACCOUNTS_TABLE_HEADER = String.format(
        "+---------------+----------+--------+%n" +
        "|Account Number |Balance   |Currency|%n" +
        "+---------------+----------+--------+%n"
    );
    private static final String ACCOUNTS_TABLE_ROW_SEPARATOR = String.format(
        "+---------------+----------+--------+%n");

    // Transactions table format (statements)
    private static final String TRANSACTIONS_TABLE_CONTENT_FORMAT = "|%-23s|%-20s|%-13s|%-10s|%n";
    private static final String TRANSACTIONS_TABLE_HEADER = String.format(
            "+-----------------------+--------------------+-------------+----------+%n"+
            "|          Date         |        Payee       |    Amount   | Currency |%n"+
            "+-----------------------+--------------------+-------------+----------+%n"
    );
    private static final String TRANSACTIONS_TABLE_ROW_SEPARATOR = String.format(
            "+-----------------------+--------------------+-------------+----------+%n"
    );

    // Create loans table
    public static String createLoansTable(ArrayList<BankLoan> loansList) {
        String loansTable = LOAN_TABLE_HEADER;

        for (BankLoan bankLoan : loansList) {
            loansTable += String.format(LOAN_TABLE_CONTENT_FORMAT,
                                        bankLoan.getLoanId(),
                                        bankLoan.getAccount().getAccountNumber(),
                                        bankLoan.getAmountLoaned() + " " + bankLoan.getCurrency().getCurrencyId(),
                                        bankLoan.getReason(),
                                        bankLoan.getInterestRate() + "%",
                                        bankLoan.getStatus());
            loansTable += LOAN_TABLE_ROW_SEPARATOR;
        }

        return loansTable;
    }

    // Accounts table
    public static String createAccountsTable(ArrayList<Account> accountsList){
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

    // Transactions table
    public static String createTransactionsTable(ArrayList<Transaction> transactions) {
        String transactionsTable = TRANSACTIONS_TABLE_HEADER;

        for(Transaction transaction: transactions){
            transactionsTable += String.format(TRANSACTIONS_TABLE_CONTENT_FORMAT,
                    transaction.getTimestamp().toString(),
                    transaction.getPayee(),
                    transaction.getAmount(),
                    transaction.getCurrency().getCurrencyId().toUpperCase());
            transactionsTable += TRANSACTIONS_TABLE_ROW_SEPARATOR;
        }
        return transactionsTable;
    }

}