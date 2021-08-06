package server.bank;

import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import server.database.DbUtils;
import server.support.InputProcessor;
import server.user.Customer;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EthereumUtils {

    static String pathToWallets = "./ethereum_wallets";

    // connection to Infura API
    static Web3j web3 = Web3j.build(new HttpService("https://goerli.infura.io/v3/b9b8a5c7c40041d5a8bd82b921f378c9"));

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
                out.println("Create a separate password for your Ethereum Wallet");
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
                    out.println("Passwords do not match, please re-enter");
                }
            }
        } catch (IOException | CipherException e) {
            e.printStackTrace();
        }

        return "Ethereum wallet successfully created with address: " + address;
    }

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
     * Method to return the balance of an address in the Ethereum network
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

        String recipientAddress = null;
        try {
            out.println("Please enter the address you would like to send Ether to send");

            Pattern validEthereumAddress = Pattern.compile("([a-zA-Z0-9]{42})");
            Matcher m;

            do {
                recipientAddress = in.readLine();
                m = validEthereumAddress.matcher(recipientAddress);

                if(!m.matches()) {
                    out.println("Not a valid Ethereum Address, please re-enter");
                }
            } while (!m.matches());

            out.println("Please enter the amount you would like to send (in Ether) Your current balance is: " + getAddressBalance(senderAddress) + " Eth");
            double amountToSend = 0.0;

            do {
                amountToSend = Double.parseDouble(in.readLine());

                //if amountToSend is greater than the senders balance
                if(new BigDecimal(amountToSend).compareTo(getAddressBalance(senderAddress)) > 0){
                    out.println("You do not have the funds required for this transaction. Please enter an amount that is equal to or less than your current balance of: " + getAddressBalance(senderAddress) + " Eth");
                }

            } while (new BigDecimal(amountToSend).compareTo(getAddressBalance(senderAddress)) > 0);

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

            //get TransactionHash
            String transactionHash = ethSendTransaction.getTransactionHash();

            response = "Transaction submitted to the network" + "\n" +
                       "To check the transaction status visit: " + "\n" +
                       "https://goerli.etherscan.io/tx/" + transactionHash + "\n";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}
