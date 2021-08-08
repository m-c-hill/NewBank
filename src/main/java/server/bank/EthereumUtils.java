package server.bank;

import org.jetbrains.annotations.NotNull;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import server.database.DbUtils;
import server.support.InputProcessor;
import server.user.Customer;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility class providing functionality relating to Ethereum wallet management
 */
public class EthereumUtils {

    static String pathToWallets = "./ethereum_wallets";

    // connection to Infura API
    static Web3j web3 = Web3j.build(new HttpService("https://goerli.infura.io/v3/b9b8a5c7c40041d5a8bd82b921f378c9"));

    /**
     * Method to create a new Ethereum Wallet
     * @param customer The {@link Customer} object representing the customer currently logged in.
     * @param in {@link BufferedReader} used to receive customer input
     * @param out {@link PrintWriter} used to output to the console
     * @return String The result of the wallet creation
     */
    public static String createEthereumWallet(Customer customer, BufferedReader in, PrintWriter out) {

        Credentials credentials = null;
        String address = "";

        int userId = customer.getUserID();
        String customerFirstName = customer.getFirstName();
        out.println("Creating Ethereum Wallet for " + customerFirstName);

        // temporary storage for wallet files.
        File walletDirectory = new File(pathToWallets);

        // take user password and create wallet file
        boolean success = false;

        try {

            while(!success) {
                out.println("Create a new password for your Ethereum Wallet");
                String password1 = in.readLine();
                out.println("Re-enter password");
                String password2 = in.readLine();

                // check passwords match
                if(InputProcessor.doPasswordsMatch(password1, password2)){
                    // if passwords match create a wallet file
                    out.println("Passwords match, creating Ethereum wallet...");
                    Bip39Wallet wallet = WalletUtils.generateBip39Wallet(password1, walletDirectory);

                    // store wallet file
                    DbUtils dbUtils = new DbUtils(out);
                    dbUtils.storeEthereumWallet(userId, wallet);

                    File walletFile = new File(walletDirectory + "/" + wallet.getFilename());

                    // get wallet address
                    credentials = WalletUtils.loadCredentials(password1, walletFile);
                    address = credentials.getAddress();

                    // Wallet file contents have been stored in the database we can now remove the file.
                    walletFile.delete();
                    success = true;
                } else {
                    // passwords do not match try again
                    out.println("Passwords do not match. Try again.");
                }
            }
        } catch (IOException | CipherException e) {
            e.printStackTrace();
        }

        return "Ethereum wallet successfully created with address: " + address;
    }

    /**
     * A method to retrieve a customers Ethereum wallet information
     * @param customer The {@link Customer} object representing the customer currently logged in.
     * @param in {@link BufferedReader} used to receive customer input
     * @param out {@link PrintWriter} used to output to the console
     * @return String containing customer name, user ID, Ethereum address and balance.
     */
    public static String showEthereumWalletInfo(Customer customer, BufferedReader in, PrintWriter out) {

        Credentials credentials = null;
        BigDecimal balance = null;

        // retrieve customers Ethereum wallet from database
        try {
            DbUtils dbUtils = new DbUtils(out);
            credentials = dbUtils.retrieveEthereumCredentials(customer.getUserID(), in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(credentials == null){
            return "Unable to retrieve wallet file";
        }

        String address = credentials.getAddress();

        // get wallet balance
        balance = getAddressBalance(address);

        return  "Customer: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
                "User ID: " + customer.getUserID() + "\n" +
                "Ethereum Wallet Address: " + address + "\n" +
                "Address Balance: " + balance + " Ether";
    }

    /**
     * A helper method to return the balance of an address in the Ethereum network
     * @param address the address to check
     * @return
     */
    private static BigDecimal getAddressBalance(String address) {
        try {
            return Convert.fromWei(web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()
                    .getBalance().toString(), Convert.Unit.ETHER);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * A method to transfer Ether from one Ethereum address to another.
     * @param customer a Customer object representing the sender
     * @param in {@link BufferedReader}
     * @param out {@link PrintWriter}
     * @return String with transaction details.
     */
    public static String transferEther(Customer customer, BufferedReader in, PrintWriter out) {

        Credentials credentials = null;
        String response = "";

        try {
            DbUtils dbUtils = new DbUtils(out);
            credentials = dbUtils.retrieveEthereumCredentials(customer.getUserID(), in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(credentials == null){
            return "Unable to retrieve wallet file";
        }

        String senderAddress = credentials.getAddress();

        try {
            String recipientAddress = InputProcessor.takeValidEthereumAddress(in, out);

            out.println("Please enter the amount you would like to send (in Ether) Your current balance is: " + getAddressBalance(senderAddress) + " Eth");
            double amountToSend = 0.0;

            do {
                amountToSend = Double.parseDouble(in.readLine());

                //if amountToSend is greater than the senders balance
                if(new BigDecimal(amountToSend).compareTo(getAddressBalance(senderAddress)) > 0){
                    out.println("You do not have the funds required for this transaction. Please enter an amount that is equal to or less than your current balance of: " + getAddressBalance(senderAddress) + " Eth");
                }

            } while (new BigDecimal(amountToSend).compareTo(getAddressBalance(senderAddress)) > 0);

            String transactionHash = createTransaction(credentials, senderAddress, recipientAddress, amountToSend);

            response = "Transaction submitted to the network" + "\n" +
                       "To check the transaction status visit: " + "\n" +
                       "https://goerli.etherscan.io/tx/" + transactionHash + "\n";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * A helper method to create an Ethereum transaction
     * @param credentials the {@link Credentials} object for the currently logged in user
     * @param senderAddress the address to send the Ether from
     * @param recipientAddress the address to send to Ether to
     * @param amountToSend the amount of Ether to send
     * @return the hash of the transaction
     * @throws IOException
     */
    private static String createTransaction(Credentials credentials, String senderAddress, String recipientAddress, double amountToSend) throws IOException {

        // Get the latest nonce of current account
        EthGetTransactionCount ethGetTransactionCount = web3.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).send();
        BigInteger nonce = ethGetTransactionCount.getTransactionCount();

        // Value to transfer (in wei)
        BigInteger value = Convert.toWei(String.valueOf(amountToSend), Convert.Unit.ETHER).toBigInteger();

        // Gas Parameter
        BigInteger gasLimit = BigInteger.valueOf(21000);
        BigInteger gasPrice = Convert.toWei("100", Convert.Unit.GWEI).toBigInteger();

        // Prepare the rawTransaction
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, recipientAddress, value);

        // Sign the transaction
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        // Send transaction
        EthSendTransaction ethSendTransaction = web3.ethSendRawTransaction(hexValue).send();

        // return TransactionHash
        return ethSendTransaction.getTransactionHash();
    }

}
