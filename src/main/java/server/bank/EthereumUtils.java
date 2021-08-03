package server.bank;

import org.web3j.crypto.Bip39Wallet;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.WalletUtils;
import server.database.DbUtils;
import server.support.InputProcessor;
import server.user.Customer;

import java.io.*;

public class EthereumUtils {

    static String pathToWallets = "./ethereum_wallets";
//    private BufferedReader in;
//    private PrintWriter out;
//
//    public EthereumUtils(BufferedReader in, PrintWriter out) throws IOException {
//        this.in = in;
//        this.out = out;
//    }

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
}
