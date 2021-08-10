# NewBank

## Description

## Getting Started

### Dependencies
* Java
* Maven
  * To install Maven, run: ```sudo apt-get install maven```

### Installing
* Clone the NewBank repo: ```git clone https://github.com/bnaylor1989/NewBank.git```
* Create a new directory ".secrets" within the NewBank directory: ```mkdir .secrets```
* Copy the following files into the .secrets directory (required to access the AWS database and Twilio API):
  * aws_credentials.json
  * twilio_credentials.json
* Please find a link to these files in the SE2 Group 8 Submission Document or email mch86@bath.ac.uk to request a link.

### Running NewBank
While in the NewBank project directory:
* Check the Maven is installed: ```mvn --version```
* Compile the project: ```mvn package```
* Start the NewBank server: ```java -cp target/NewBank-1.0-SNAPSHOT.jar server.bank.NewBankServer```
  * If successful, the message ```New Bank Server listening on 14002``` should appear in your terminal
* Known bug: you may receive a "JNI error" after running the server command with the following message: ```Exception in thread "main" java.lang.SecurityException: Invalid signature file digest for Manifest main attributes```.
  * To fix this issue, run the following command to fix the issue: ```zip -d target/NewBank-1.0-SNAPSHOT.jar 'META-INF/.SF' 'META-INF/.RSA' 'META-INF/*SF'```
  * Then rerun the above server command.
  * This will be addressed in future releases.
* With the server running, start a new terminal.
* Start the NewBank client: ```java -cp target/NewBank-1.0-SNAPSHOT.jar client.ExampleClient```
  * If successful, this should bring up the
  
![Main menu](https://imgur.com/9omUzGo.png)

### Login IDs
The AWS database has been populated with a number of "dummy" records (users, accounts and transactions) to allow each 
of the NewBank features to be tested first hand.  
To access these accounts, the following dummy logins and passwords have been created:

####Customer 1
```
- Login: jeremy  
- Password: pw_jeremy
```

####Customer 2
```
- Login: alan  
- Password: pw_alan
```

####Admin 1
```
- Login: simon
- Password: pw_simon
- Role: Loan manager
```

####Admin 2
```
- Login: april
- Password: pw_april
- Role: Bank manager
```

####Admin 3
```
- Login: Sophie
- Password: pw_sophie
- Role: Account manager
```

Please see the Guide below for full details on what each type of user has access to and what tasks they can carry out.

## Guide

### Menu Structure Overview

<pre>
NewBank Main Menu
├─── Login as Customer
│   ├─── Accounts
│   │   ├─── Show my accounts
│   │   ├─── Withdraw amount
│   │   ├─── Deposit amount
│   │   ├─── Create a new account
│   │   └─── Close an account
│   │
│   ├─── Statements
│   │   ├─── View my recent transactions
│   │   └─── Request statement email
│   │
│   ├─── Loans
│   │   ├─── Request a loan
│   │   ├─── View my loan status
│   │   └─── Make loan payment
│   │
│   ├─── Ethereum wallet
│   │   ├─── Create Ethereum wallet
│   │   ├─── Show Ethereum wallet
│   │   └─── Transfer Ether
│   │
│   └─── Reset my password
│
├─── Register for a New Customer Account
│
├─── Login as Admin
│   ├─── View active loans
│   └─── Manage loan requests
│
└─── Recover Account
    ├─── Forgotten account login
    └─── Forgotten account password
</pre>

### Customers
Customers of NewBank are able to open multiple accounts in a variety of currencies (both standard and crypto). The 
application allows users to withdraw and deposit cash, request loans up to a set limit and view their recent 
transactions.  

Here are some brief examples of tasks that customers can carry out, using Jeremy Usborne as an example:  

View accounts  
![view accounts](https://imgur.com/RmItGVh.png)

Deposit cash  
![deposit cash](https://imgur.com/puEZuQK.png)

View recent transactions  
![view transactions](https://imgur.com/k3VfTg0.png)

Request a loan  
![request a loan](https://imgur.com/xicQCp3.png)

View a loan status  
![view loan status](https://imgur.com/mzzdjxi.png)


### Admins
Admins are able to view active loan requests and approve/reject them. Future updates will allow admins to 
also open new accounts for users, close accounts and view user statements. Different permissions are granted to admin 
staff depending on their role within the bank (ie. loan manager, account manager). These permissions provide access 
privileges for different activities within the application (for example, account managers would not be allowed to grant
loans). Using the dummy logins provided, you'll see that April (a bank manager) is not able to access the loans menu,
while Simon (a loan manager) is.  

April's loan access  
![April loan access](https://imgur.com/GgVIre7.png)

Simon's loan access  
![Simon loan access](https://imgur.com/jjr2dPa.png)


### **Ethereum Wallet Functionality:**

#### **Accessing API**

![Ethereum Client architecture](https://i.imgur.com/HUV85EX.png)

The wallet connects to an API provider called Infura that handles the actual communication with the Ethereum network <br>
Log in here:
https://infura.io/login (Credentials provided separately.)

Click Ethereum in the left toolbar

![Main account dashboard](images/Screenshot1.png)

This screen shows you the projects created for this account. It also displays some overall stats for the account.
As you can see the API allows 100,000 transactions a day.

Select the NewBank project to see a breakdown of the types of requests made and when they were made. 

![Main account dashboard](images/Screenshot2.png)

### Crediting your address with test Ether

This application uses an Ethereum test network called Goerli, so we don't need real Ether to use it.
In order to credit an address (think account number in traditional finance) with test Ether you need to use what is known as a faucet.
You can find the Goerli faucet here:
https://www.rinkeby.io/#faucet <br>
It's very straightforward and instructions on how to use are on the page.
However there are already two users in the database with already credited wallets.

### Test Users

Name: Jack Ryan <br>
Login Credentials: <br>
Login ID: jack1970 <br>
Password: password 
Ethereum Address: 0xfbdefb7dde102721efba7570572b6c12b6df887a
Ethereum Password: password

===========================

Name: Josie Miller <br>
Login Credentials:<br>
Login ID: jmiller <br>
Password: password <br>
Ethereum Address: 0xee3bb22de90c5d2319a9a6dfa7e0d69098892d52 <br>
Ethereum Password: password

Note: users are required to set up a separate password to access their Ethereum wallet. <br>
When Ethereum wallets are created they are stored in the ethereum_wallet table using the userId as a primary key.

### Ethereum Functionality within NewBank

From the main menu there are 3 options relating to Ethereum: <br>
 - Create Ethereum Wallet - Allows the user to create a new Ethereum Wallet
 - Show Ethereum Wallet - Retrieve the address and current balance of an Ethereum Wallet
 - Transfer Ether - Sends Ether from the currently logged-in users address to another specified address. Following the steps outlined will produce what is known as a transaction 
hash, which can be viewed using a block explorer as described below.

### Blockchain Explorer

You can copy any wallet address or transaction hash and paste it into the test networks block explorer: <br>
https://goerli.etherscan.io/ <br>
This will allow you to view information relating to specific transactions and address and proves that the application is communicating with the real external network.

For example these are the addresses of our two test users:
 - Jack Ryan - https://goerli.etherscan.io/address/0xfbdefb7dde102721efba7570572b6c12b6df887a
 - Josie Miller - https://goerli.etherscan.io/address/0xee3bb22de90c5d2319a9a6dfa7e0d69098892d52
 - Here is a transaction where I transferred 3 Eth from Jack to Josie - https://goerli.etherscan.io/tx/0x36d55c88fbba351b0a61831a1846b8c603bb5e2d22a9b6c7bdc9e5020a37ccf6

### Useful resources:

Web3j Java library docs - http://docs.web3j.io/latest/ <br>
Infura API docs - https://infura.io/docs/ethereum <br>

Useful blog posts:

https://greg.jeanmart.me/2020/05/01/manage-an-ethereum-account-with-java-and-web3j/# <br>
https://medium.datadriveninvestor.com/block-chain-using-java-and-ethereum-part-i-c225f33064d8 <br>
https://medium.com/datadriveninvestor/block-chain-using-javas-web3j-and-ethereum-part-ii-create-or-open-an-account-896cf3b5ef12 <br>
https://medium.datadriveninvestor.com/blockchain-using-java-part-iii-transaction-of-ethers-from-one-account-to-another-using-web3j-861c39e7a5e1 <br>












