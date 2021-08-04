package server.bank;

import com.amazonaws.services.dynamodbv2.xspec.S;
import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import server.database.DbUtils;
import server.support.InputProcessor;
import server.user.Customer;

import java.io.*;
import java.math.BigDecimal;

public class EthereumUtils {

    static String pathToWallets = "./ethereum_wallets";

    // connection to Infura API
    static Web3j web3 = Web3j.build(new HttpService("https://rinkeby.infura.io/v3/b9b8a5c7c40041d5a8bd82b921f378c9"));

    public static String createEthereumWallet(Customer customer, BufferedReader in, PrintWriter out) {

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

                    // Wallet file contents have been stored in the database,
                    // we can now remove the file.
                    File walletFile = new File(walletDirectory + "/" + wallet.getFilename());
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

        return "Ethereum wallet successfully created";
    }

    public static String showEthereumWallet(Customer customer, BufferedReader in, PrintWriter out) {

        String walletContents = "";
        Credentials walletCredentials = null;
        BigDecimal balance = null;

        // retrieve customers Ethereum wallet from database
        try {
            DbUtils dbUtils = new DbUtils(out);
            walletContents = dbUtils.retrieveEthereumWallet(customer.getUserID());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // create a Credentials object from the wallet contents
        // this requires the user tp enter their separate Ethereum wallet password
        out.println("Please enter your separate Ethereum wallet password");
        try {
            String walletPassword = in.readLine();
            walletCredentials = WalletUtils.loadJsonCredentials(walletPassword, walletContents);
        } catch (IOException | CipherException e) {
            e.printStackTrace();
        }

        // wallet address
        assert walletCredentials != null;
        String address = walletCredentials.getAddress();

        // get wallet balance
        try {
            balance = Convert.fromWei(web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send()
                    .getBalance().toString(), Convert.Unit.ETHER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  "Customer: " + customer.getFirstName() + " " + customer.getLastName() + "\n" +
                "User ID: " + customer.getUserID() + "\n" +
                "Ethereum Wallet Address: " + address + "\n" +
                "Address Balance: " + balance + " Ether";
    }

    public static String transferEther(Customer customer, BufferedReader in, PrintWriter out) {

        return "";
    }

}
