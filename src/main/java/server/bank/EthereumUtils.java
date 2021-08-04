package server.bank;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import server.database.DbUtils;
import server.support.InputProcessor;
import server.user.Customer;

import java.io.*;
import java.math.BigDecimal;
import java.util.Optional;

public class EthereumUtils {

    static String pathToWallets = "./ethereum_wallets";

    // connection to Infura API
    static Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/b9b8a5c7c40041d5a8bd82b921f378c9"));

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

        // wallet address
        assert credentials != null;
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
        Optional<TransactionReceipt> transactionReceipt;
        String response = "";

        try {
            DbUtils dbUtils = new DbUtils(out);
            credentials = dbUtils.retrieveEthereumCredentials(customer.getUserID(), in);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String senderAddress = credentials.getAddress();

        String recipientAddress = null;
        try {
            out.println("Please enter the address you would like to send Ether to send");
            recipientAddress = in.readLine();

            out.println("Please enter the amount you would like to send (in Ether) Your current balance is: " + getAddressBalance(senderAddress));
            long amountToSend = Long.parseLong(in.readLine());
            transactionReceipt = Optional.ofNullable(Transfer.sendFunds(web3, credentials, recipientAddress, BigDecimal.valueOf(amountToSend), Convert.Unit.ETHER).send());

            int count = 0;
            do {
                if(transactionReceipt.isPresent()){
                    out.println("Transaction Successfully Mined");
                    break;
                } else {
                    out.println("Please wait for transaction to be mined...");
                    count++;
                    Thread.sleep(3000);
                }
            } while (count > 10); // after 30 seconds exit loop

            String transactionHash = transactionReceipt.get().getTransactionHash();
            //TODO Return more transaction details here.
            // maybe a link to the transaction, e.g. - https://rinkeby.etherscan.io/tx/0x9622b2297f32dd3af30f02b584c8a61236eceafe9fc4fbc3b4316e98e5a91b5c

            response = "Transaction Hash: " + transactionHash;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

}
