import java.util.*;
import java.sql.*;
public class App {
    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "Leslie14")){ //Connects to databse with port 9400, username postgres and Password Landa123
            if (conn != null) {

                Scanner input = new Scanner(System.in); //User Input for Account
                System.out.println("Enter username:"); //Account Username
                String uname = input.nextLine();
                System.out.println("Enter password:"); //Account Password
                String pword = input.nextLine();
                
                Statement st = conn.createStatement(); // GETS ROWS FROM CUSTOMER TABLE INTO rs
                ResultSet rs = st.executeQuery("SELECT * FROM customer WHERE username = '" + uname + "' AND password = '" + pword + "'"); // lst.get(3) and (4) are Username and Password
                int length = rs.getMetaData().getColumnCount();

                Statement st2 = conn.createStatement(); // GETS ROWS FROM EMPLOYEE AND BRANCH TABLE INTO rs2
                ResultSet rs2 = st2.executeQuery("SELECT * FROM employee CROSS JOIN branch WHERE worksat = branch.address AND username = '" + uname + "' AND password = '" + pword + "'"); // lst.get(5) and (6) are Username and Password
                int length2 = rs2.getMetaData().getColumnCount();
 
                String type1 = null; // Checks if user is part of the customer table
                if(rs.next()){
                    type1 = rs.getString(1);
                }

                String type2 = null; // Checks if user is part of the employee table
                if(rs2.next()){
                    type2 = rs2.getString(1);
                }

                List<String> lst = new ArrayList<String>(); //list of customer details
                List<String> accnts = new ArrayList<String>(); //list of accounts the customer owns
                List<String> accntIDs = new ArrayList<String>(); //list of customers account ids 
                List<String> banks = new ArrayList<String>(); //list of customers banks 
                List<String> perms = new ArrayList<String>(); //list of permissions allowed by current user
                
                Dictionary<List<String>, String> allAccnts = new Hashtable<List<String>, String>(); //Dictionary of key [aid, bank] and value balance

                String type = null;
                String username = "";
                String name = "";
                
                if(type1 != null){ //Gets the type of customer (only can be customer)
                    lst.add(type1);
                    for(int i = 2; i <= length; i++){ //Makes a ArrayList of all of customers details 
                        lst.add(rs.getString(i));
                    }

                    type = "Customer";
                    name = lst.get(1); //name of the user
                    username = lst.get(3); //username of the user
                    String cid = lst.get(0); //gets the ID of the customer
                    System.out.println("----------------------");
                    System.out.println(name + " Succuesfully Logged in as a Customer"); //Prints the name of customer
                    System.out.println("Username: " + username); //Prints the username of customer
                    System.out.println("----------------------");
                    
                    Statement st3 = conn.createStatement(); // GETS ROWS FROM account TABLE INTO rs3 WHERE THE CUSTOMER OWNS THE ACCOUNT
                    ResultSet rs3 = st3.executeQuery("SELECT * FROM account WHERE customerID = '" + cid + "'"); 
                    int length3 = rs3.getMetaData().getColumnCount();
                    System.out.println("--Accounts--");
                    while(rs3.next()){ 
                        String temp1 = "";
                        List<String> accnt = new ArrayList<String>(); // Details of Each individual Bank Account
                        List<String> pair = new ArrayList<String>(); //Pair of Account ID and Bank 
                        for (int i = 1; i <= length3; i++){
                            accnts.add(rs3.getString(i)); //adds each value from table into accnts
                            accnt.add(rs3.getString(i)); //adds each value from table into accnt
                            if(i == 1){
                                accntIDs.add(rs3.getString(i)); //adds all account IDs of the user to accntIds
                                pair.add(rs3.getString(i)); //adds account IDs to the pair
                            }
                            if(i == 2){
                                temp1 = rs3.getString(i); //gets the balance of account
                            }
                            if(i == 5){
                                banks.add(rs3.getString(i)); //adds all the banks that the customer has an account in
                                pair.add(rs3.getString(i)); //adds the bank to the pair
                                allAccnts.put(pair, temp1); //puts the pairs of aIDs and banks into the dictionary as a key with balance as the value
                            }
                        }
                        System.out.println("Account ID: " + accnt.get(0) +" | Balance: " + accnt.get(1) + " | Type: " + accnt.get(2) + " | " + accnt.get(4)); //Prints the information of each account 
                    }
                    System.out.println("----------------------");

                } else if (type2 != null){ // Gets the type of employee
                    type = type2; //Sets the type of the employee
                    lst.add(type2);
                    for(int i = 2; i <= length2; i++){ //Makes a Arraylist of employees details
                        lst.add(rs2.getString(i));
                    }
                    name = lst.get(1); //gets the employees name
                    username = lst.get(5); //gets the employees username
                    String bank = lst.get(9); //gets where the employee works
                    System.out.println("----------------------");
                    System.out.println(name + " Succuesfully Logged in as a " + type); //Prints the name and type of employee
                    System.out.println("Username: " + username); //Prints the username
                    System.out.println("----------------------");

                    Statement st3 = conn.createStatement(); // GETS ROWS FROM EMPLOYEE TABLE INTO rs2
                    ResultSet rs3 = st3.executeQuery("SELECT * FROM account WHERE bank = '" + bank + "'"); // arr[5] and [6] are Username and Password
                    int length3 = rs3.getMetaData().getColumnCount();
                    while(rs3.next()){
                        String temp1 = "";
                        List<String> accnt = new ArrayList<String>(); // Details of Each Individual Bank Account in their bank
                        List<String> pair = new ArrayList<String>(); //Pair of Account ID and Bank 
                        for (int i = 1; i <= length3; i++){
                            accnts.add(rs3.getString(i));  //adds each value from table into accnts
                            accnt.add(rs3.getString(i)); //adds each value from table into accnt
                            if(i == 1){
                                accntIDs.add(rs3.getString(i)); //adds all account IDs of the user to accntIds
                                pair.add(rs3.getString(i)); //adds account IDs to the pair
                            }
                            if(i == 2){
                                temp1 = rs3.getString(i); //gets the balance of account
                            }
                            if(i == 5){
                                pair.add(rs3.getString(i)); //adds the bank to the pair
                                allAccnts.put(pair, temp1); //puts the pairs of aIDs and banks into the dictionary as a key with balance as the value
                            }
                        }
                    }
                } else {
                    System.out.println("Account Doesn't Exist"); // Prints if the account doesn't exist
                    System.exit(1);
                }
                
                if(type.equals("Customer")){ //Sets the permissions of each type
                    perms.add("Withdraw");
                    perms.add("Deposit");
                    perms.add("Transfer");
                    perms.add("External Transfer");
                    perms.add("Account Management");
                    perms.add("Logout");
                } else if (type.equals("Manager")){
                    perms.add("Withdraw");
                    perms.add("Deposit");
                    perms.add("Transfer");
                    perms.add("External Transfer");
                    perms.add("Account Management");
                    perms.add("Logout");
                    perms.add("Analytics");
                } if(type.equals("Teller")){
                    perms.add("Withdraw");
                    perms.add("Deposit");
                    perms.add("Transfer");
                    perms.add("External Transfer");
                    perms.add("Logout");
                }

                while(true){
                    System.out.print("What would you like to do?: " + perms.get(0)); //Prints out all permissions of the user
                    for(int i = 1; i < (perms.size()); i++){
                        System.out.print(", " + perms.get(i));
                    }

                    System.out.println("");
                    System.out.println("----------------------");
                    String action = input.nextLine(); //Gets what action user wants to do (Withdraw, Transfer...)
                    String account = "";
                    String accountTo = "";
                    String bank = "";
                    String bankTo = "";

//---------------------------------------------------------------------------------------------------------------------- WITHDRAW
                    if(action.equals("Withdraw") && perms.contains("Withdraw")){
                        if(type.equals("Customer")){ //Gets the bank that the customer wants to withdraw from
                            while(true){
                                System.out.println("----------------------");
                                System.out.println("What bank would you like to withdraw from?");
                                bank = input.nextLine(); //Bank they want to withdraw from
                                if(banks.contains(bank) || bank.equals("Skip")){ //If the bank exists within their banks, it continues, else it tries again, Skip skips the action and goes back
                                    break;
                                } else {
                                    System.out.println("You do not own an account in this bank!");
                                }   
                            }
                        } else {
                            bank = lst.get(9); //Sets the employees bank to the bank they work at
                        }

                        while(!bank.equals("Skip")){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to withdraw from?");
                            account = input.nextLine(); //Gets the account number they want to withdraw from
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            if((allAccnts.get(pair) != null) || account.equals("Skip")){
                                break; //Checks if the pair of account exists within their accessible account, or if they want to skip
                            } else {
                                System.out.println("Account is not yours!");
                            }   
                        }
                        
                        while(!(account.equals("Skip") || bank.equals("Skip"))){ //Checks if previously skipped
                            System.out.println("----------------------");
                            System.out.println("How much $ would you like to withdraw?");
                            String money = input.nextLine(); //Withdraw amount
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            if(Float.parseFloat(allAccnts.get(pair)) >= Float.parseFloat(money) && Float.parseFloat(money) >= 0){ //Checks if there is enough money in the account
                                Statement st4 = conn.createStatement(); //Updates balance in database
                                st4.executeUpdate("UPDATE account SET balance = balance - " + money + " WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                ResultSet rtID = st4.executeQuery("SELECT count(*) FROM transaction"); //gets the amount of transactions and sets the
                                rtID.next(); String tID = rtID.getString(1); 
                                st4.executeUpdate("INSERT INTO transaction VALUES (" + tID + ", " + money + ", CURRENT_DATE, 'withdraw', " + account + ", null, '" + bank + "')");
                                ResultSet rs4 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                rs4.next(); 
                                String newBal = (rs4.getString(1)); //Gets new balance from database
                                allAccnts.put(pair,newBal); //Updates balance in dictionary
                                st4.close();
                                System.out.println("New balance is: $" + newBal);
                                System.out.println("----------------------");
                                break;
                            } else {
                                if(!(Float.parseFloat(money) >= 0)){
                                    System.out.println("Cannot be Negative!");
                                    break;
                                } else {
                                    System.out.println("Not enough money!");
                                    break;
                                }
                            }   
                        }
//---------------------------------------------------------------------------------------------------------------------- DEPOSIT
                    } else if(action.equals("Deposit") && perms.contains("Deposit")){
                        if(type.equals("Customer")){ //Gets the bank that the customer wants to withdraw from
                            while(true){
                                System.out.println("----------------------");
                                System.out.println("What bank would you like to deposit into?");
                                bank = input.nextLine(); //Bank they want to deposit into
                                if(banks.contains(bank) || bank.equals("Skip")){ //If the bank exists within their banks, it continues, else it tries again, Skip skips the action and goes back
                                    break;
                                } else {
                                    System.out.println("You do not own an account in this bank!");
                                }   
                            }
                        } else {
                            bank = lst.get(9); //Sets the employees bank to the bank they work at
                        }

                        while(!bank.equals("Skip")){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to deposit into?");
                            account = input.nextLine(); //Gets the account number they want to deposit into
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            if((allAccnts.get(pair) != null) || account.equals("Skip")){
                                break; //Checks if the pair of account exists within their accessible account, or if they want to skip
                            } else {
                                System.out.println("Account is not yours!");
                            }   
                        }
                        
                        while(!(account.equals("Skip") || bank.equals("Skip"))){ //Checks if previously skipped
                            System.out.println("----------------------");
                            System.out.println("How much $ would you like to deposit?");
                            String money = input.nextLine(); //Deposit amount
                            if(!(Float.parseFloat(money) >= 0)){
                                System.out.println("Cannot be Negative!");
                                break;
                            }
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            Statement st4 = conn.createStatement(); //Updates balance in database
                            st4.executeUpdate("UPDATE account SET balance = balance + " + money + " WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                            ResultSet rsID = st4.executeQuery("SELECT count(*) FROM transaction"); //Selects total amount of transaction and sets the tID to that
                            rsID.next(); String sID = rsID.getString(1); 
                            st4.executeUpdate("INSERT INTO transaction VALUES (" + sID + ", " + money + ", CURRENT_DATE, 'deposit', " + account + ", null, '" + bank + "')");
                            ResultSet rs4 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                            rs4.next(); 
                            String newBal = (rs4.getString(1)); //Gets new balance from database
                            allAccnts.put(pair,newBal); //Updates balance in dictionary
                            st4.close();
                            System.out.println("New balance is: $" + newBal);
                            System.out.println("----------------------");
                            break;
                        }  
//---------------------------------------------------------------------------------------------------------------------- TRANFER
                    } else if(action.equals("Transfer") && perms.contains("Transfer")){
                        if(type.equals("Customer")){ //Gets the bank that the customer wants to transfer from
                            while(true){
                                System.out.println("----------------------");
                                System.out.println("What bank would you like to transfer from?");
                                bank = input.nextLine(); //Bank they want to transfer from
                                if(banks.contains(bank) || bank.equals("Skip")){ //If the bank exists within their banks, it continues, else it tries again, Skip skips the action and goes back
                                    break;
                                } else {
                                    System.out.println("You do not own an account in this bank!");
                                }   
                            }
                        } else {
                            bank = lst.get(9); //Sets the employees bank to the bank they work at
                        }

                        Dictionary<List<String>, String> bankAccnts = new Hashtable<List<String>, String>(); //Dictionary of key [aid, bank] and value balance
                        Statement st5 = conn.createStatement(); // GETS ROWS FROM EMPLOYEE TABLE INTO rs2
                        ResultSet rs5 = st5.executeQuery("SELECT * FROM account WHERE bank = '" + bank + "'"); // arr[5] and [6] are Username and Password
                        int length5 = rs5.getMetaData().getColumnCount();
                        while(rs5.next()){
                            String temp1 = "";
                            List<String> pair = new ArrayList<String>(); //Pair of Account ID and Bank 
                            for (int i = 1; i <= length5; i++){
                                if(i == 1){
                                    pair.add(rs5.getString(i)); //adds account IDs to the pair
                                }
                                if(i == 2){
                                    temp1 = rs5.getString(i); //gets the balance of account
                                }
                                if(i == 5){
                                    pair.add(rs5.getString(i)); //adds the bank to the pair
                                    bankAccnts.put(pair, temp1); //puts the pairs of aIDs and banks into the dictionary as a key with balance as the value
                                }
                            }
                        }
                        st5.close();
                        while(!bank.equals("Skip")){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to transfer from?");
                            account = input.nextLine(); //Gets the account number they want to transfer from
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            if((allAccnts.get(pair) != null) || account.equals("Skip")){
                                break; //Checks if the pair of account exists within their accessible account, or if they want to skip
                            } else {
                                System.out.println("Account is not yours!");
                            }   
                        }
                        
                        while(!(account.equals("Skip") || bank.equals("Skip"))){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to transfer to?");
                            accountTo = input.nextLine(); //Gets the account number they want to transfer to
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(accountTo); pair.add(bank); //Makes a list of [account, bank]                   
                            if((bankAccnts.get(pair) != null) || accountTo.equals("Skip")){
                                break; //Checks if the pair of account exists within the bank, or if they want to skip
                            } else {
                                System.out.println("Account doesnt exist in this bank!");
                            }   
                        }

                        while(!(account.equals("Skip") || bank.equals("Skip") || accountTo.equals("Skip"))){ //Checks if previously skipped
                            System.out.println("----------------------");
                            System.out.println("How much $ would you like to transfer?");
                            String money = input.nextLine(); //Withdraw amount
                            if(!(Float.parseFloat(money) >= 0)){
                                System.out.println("Cannot be Negative!");
                                break;
                            }
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            List<String> pair2 = new ArrayList<String>(); 
                            pair2.add(accountTo); pair2.add(bank); //Makes a list of [account, bank]
                            if(Float.parseFloat(allAccnts.get(pair)) >= Float.parseFloat(money)){ //Checks if there is enough money in the account
                                Statement st4 = conn.createStatement(); //Updates balance in database
                                st4.executeUpdate("UPDATE account SET balance = balance - " + money + " WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                st4.executeUpdate("UPDATE account SET balance = balance + " + money + " WHERE accountID = " + accountTo + " AND bank = '" + bank + "'"); 
                                ResultSet rtID = st4.executeQuery("SELECT count(*) FROM transaction"); //gets the amount of transactions and sets the
                                rtID.next(); String tID = rtID.getString(1); 
                                st4.executeUpdate("INSERT INTO transaction VALUES (" + tID + ", " + money + ", CURRENT_DATE, 'transfer', " + account + ", " + accountTo + ", '" + bank + "')");
                                ResultSet rs4 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                rs4.next(); 
                                String newBal = (rs4.getString(1)); //Gets new balance from database
                                ResultSet rs42 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + accountTo + " AND bank = '" + bank + "'"); 
                                rs42.next(); 
                                String newBal2 = (rs42.getString(1)); //Gets new balance from database
                                allAccnts.put(pair,newBal); //Updates balance in dictionary
                                if((allAccnts.get(pair2) != null) || accountTo.equals("Skip")){
                                    allAccnts.put(pair2,newBal2); //Updates balance in dictionary
                                }
                                st4.close();
                                System.out.println("New balance is: $" + newBal);
                                System.out.println("----------------------");
                                break;
                            } else {
                                System.out.println("Not enough money!");
                                break;
                            }   
                        }
//---------------------------------------------------------------------------------------------------------------------- EXTERNAL TRANSFER
                    } else if(action.equals("External Transfer") && perms.contains("External Transfer")){
                        if(type.equals("Customer")){ //Gets the bank that the customer wants to transfer from
                            while(true){
                                System.out.println("----------------------");
                                System.out.println("What bank would you like to transfer from?");
                                bank = input.nextLine(); //Bank they want to transfer from
                                if(banks.contains(bank) || bank.equals("Skip")){ //If the bank exists within their banks, it continues, else it tries again, Skip skips the action and goes back
                                    break;
                                } else {
                                    System.out.println("You do not own an account in this bank!");
                                }   
                            }
                        } else {
                            bank = lst.get(9); //Sets the employees bank to the bank they work at
                        }

                        Dictionary<List<String>, String> bankAccnts = new Hashtable<List<String>, String>(); //Dictionary of key [aid, bank] and value balance
                        Statement st5 = conn.createStatement(); // GETS ROWS FROM EMPLOYEE TABLE INTO rs2
                        ResultSet rs5 = st5.executeQuery("SELECT * FROM account WHERE bank = '" + bank + "'"); // arr[5] and [6] are Username and Password
                        int length5 = rs5.getMetaData().getColumnCount();
                        while(rs5.next()){
                            String temp1 = "";
                            List<String> pair = new ArrayList<String>(); //Pair of Account ID and Bank 
                            for (int i = 1; i <= length5; i++){
                                if(i == 1){
                                    pair.add(rs5.getString(i)); //adds account IDs to the pair
                                }
                                if(i == 2){
                                    temp1 = rs5.getString(i); //gets the balance of account
                                }
                                if(i == 5){
                                    pair.add(rs5.getString(i)); //adds the bank to the pair
                                    bankAccnts.put(pair, temp1); //puts the pairs of aIDs and banks into the dictionary as a key with balance as the value
                                }
                            }
                        }
                        st5.close();
                        while(!bank.equals("Skip")){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to transfer from?");
                            account = input.nextLine(); //Gets the account number they want to transfer from
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            if((allAccnts.get(pair) != null) || account.equals("Skip")){
                                break; //Checks if the pair of account exists within their accessible account, or if they want to skip
                            } else {
                                System.out.println("Account is not yours!");
                            }   
                        }
                        List<String> allBanks = new ArrayList<String>(); //list of customers banks 
                        Statement st6 = conn.createStatement(); // GETS ROWS FROM EMPLOYEE TABLE INTO rs2
                        ResultSet rs6 = st6.executeQuery("SELECT DISTINCT bank FROM branch"); // arr[5] and [6] are Username and Password
                        int length6 = rs6.getMetaData().getColumnCount();
                        while(rs6.next()){ 
                            for (int i = 1; i <= length6; i++){
                                if(i == 1){
                                    allBanks.add(rs6.getString(i)); //adds the bank to the pair
                                }
                            }
                        }

                        while(!(account.equals("Skip") || bank.equals("Skip"))){
                            System.out.println("----------------------");
                            System.out.println("What bank would you like to transfer to?");
                            bankTo = input.nextLine(); //Bank they want to transfer to
                            if(allBanks.contains(bankTo) || bankTo.equals("Skip")){ //If the bank exists, it continues, else it tries again, Skip skips the action and goes back
                                break;
                            } else {
                                System.out.println("This bank does not exist!");
                            }   
                        }

                        while(!(account.equals("Skip") || bank.equals("Skip") || bankTo.equals("Skip"))){ //Skips if previous action was skipped
                            System.out.println("----------------------");
                            System.out.println("What account would you like to transfer to?");
                            accountTo = input.nextLine(); //Gets the account number they want to transfer to
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(accountTo); pair.add(bank); //Makes a list of [account, bank]                   
                            if((bankAccnts.get(pair) != null) || accountTo.equals("Skip")){
                                break; //Checks if the pair of account exists within the bank, or if they want to skip
                            } else {
                                System.out.println("Account doesnt exist in this bank!");
                            }   
                        }

                        while(!(account.equals("Skip") || bank.equals("Skip") || bankTo.equals("Skip") || accountTo.equals("Skip"))){ //Checks if previously skipped
                            System.out.println("----------------------");
                            System.out.println("How much $ would you like to transfer?");
                            String money = input.nextLine(); //Withdraw amount
                            if(!(Float.parseFloat(money) >= 0)){
                                System.out.println("Cannot be Negative!");
                                break;
                            }
                            List<String> pair = new ArrayList<String>(); 
                            pair.add(account); pair.add(bank); //Makes a list of [account, bank]
                            List<String> pair2 = new ArrayList<String>(); 
                            pair2.add(accountTo); pair2.add(bankTo); //Makes a list of [account, bank]
                            if(Float.parseFloat(allAccnts.get(pair)) >= Float.parseFloat(money)){ //Checks if there is enough money in the account
                                Statement st4 = conn.createStatement(); //Updates balance in database
                                st4.executeUpdate("UPDATE account SET balance = balance - " + money + " WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                st4.executeUpdate("UPDATE account SET balance = balance + " + money + " WHERE accountID = " + accountTo + " AND bank = '" + bankTo + "'"); 
                                ResultSet rtID = st4.executeQuery("SELECT count(*) FROM transaction"); //gets the amount of transactions and sets the
                                rtID.next(); String tID = rtID.getString(1); 
                                st4.executeUpdate("INSERT INTO transaction VALUES (" + tID + ", " + money + ", CURRENT_DATE, 'external transfer', " + account + ", " + accountTo + ", '" + bank + "')");
                                ResultSet rs4 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + account + " AND bank = '" + bank + "'"); 
                                rs4.next(); 
                                String newBal = (rs4.getString(1)); //Gets new balance from database
                                ResultSet rs42 = st4.executeQuery("SELECT balance FROM account WHERE accountID = " + accountTo + " AND bank = '" + bankTo + "'"); 
                                rs42.next(); 
                                String newBal2 = (rs42.getString(1)); //Gets new balance from database
                                allAccnts.put(pair,newBal); //Updates balance in dictionary
                                if((allAccnts.get(pair2) != null)){
                                    allAccnts.put(pair2,newBal2); //Updates balance in dictionary
                                }
                                st4.close();
                                System.out.println("New balance is: $" + newBal);
                                System.out.println("----------------------");
                                break;
                            } else {
                                System.out.println("Not enough money!");
                                break;
                            }   
                        };
//---------------------------------------------------------------------------------------------------------------------- ACCOUNT MANAGEMENT MENU 
                    } else if(action.equals("Account Management") && perms.contains("Account Management")){
                    	System.out.println("What administrative task would you like to perform?");
                    	System.out.print("Your choices are: ");
                    	
                    	List<String> mngPrms = new ArrayList<String>(); //list of management permissions
                    	
                    	if(type.equals("Customer") || type.equals("Manager")){ 
                    		mngPrms.add("Create account");
                    		mngPrms.add("Delete account");
                    		mngPrms.add("Show statement");
                    		mngPrms.add("Pending transactions");
                    	} 
                    	if (type.equals("Manager")) {
                    		
                    		mngPrms.add("Add interest");
                    		mngPrms.add("Add overdraft fees");
                    		mngPrms.add("Add account fees");
                        }
                    	
                    	System.out.print(mngPrms.get(0)); //Prints out all permissions of the user
                        for(int i = 1; i < (mngPrms.size()); i++){
                            System.out.print(", " + mngPrms.get(i));
                        }
                        
                        System.out.println("");
                        System.out.println("----------------------");
                        String mngAction = input.nextLine(); //Gets what action user wants to do (...)
                        
//---------------------------------------------------------------------------------------------------------------------- CREATE ACCOUNT
                    	if (mngAction.equals("Create account") && mngPrms.contains("Create account")) {
                    		System.out.println("What type of account would you like to create? (Checking/Savings)");
                            System.out.println("----------------------");
                    		String acctType = input.nextLine();
                    		// Get Acct Type
                    		if (acctType.equals("Checking") || acctType.equals("Savings") ) {
                    			System.out.println("What is the desired customer ID? ()");
                                System.out.println("----------------------");
                        		int cuID = input.nextInt(); // Get customer ID
                        		
                        		System.out.println("How much money are you starting with (initial balance)? ");
                                System.out.println("----------------------");
                        		int initBalance = input.nextInt(); // Get initial balance
                        	
                        		int actNum = (int)new Random().nextInt(900) + 100; //account number generation
                        		
                        		System.out.println("Which branch would you like to open the account at (Chase/PNC)? ");
                                System.out.println("----------------------");
                        		String bnkName = input.nextLine(); // Get bank name
                        		
                        		Statement st4 = conn.createStatement();                        		
                        		st4.executeUpdate("INSERT INTO account VALUES ("+cuID+","+initBalance+",'"+acctType+"',"+actNum+",'"+bnkName+"')");
                    			
                    		} else {
                    			System.out.println("Unidentifiable response, aborting...");
                    			break;
                    		}

//---------------------------------------------------------------------------------------------------------------------- DELETE ACCOUNT       		
                    	} else if (mngAction.equals("Delete account") && mngPrms.contains("Delete account")) {
                    		//User selects account to delete
                    		System.out.println("What is the account number of the account you wish to delete? ");
                            System.out.println("----------------------");
                    		int acctNumber = input.nextInt(); // get account number
                    		//Delete account
                    		Statement st4 = conn.createStatement();                        		
                    		st4.executeUpdate("DELETE FROM account WHERE accountID=" + acctNumber);
//---------------------------------------------------------------------------------------------------------------------- SHOW STATEMENT
                    	} else if (mngAction.equals("Show statement") && mngPrms.contains("Show statement")) {
                    		System.out.println("What is the # of the account you wish to see the statement of? ");
                            System.out.println("----------------------");
                    		int acctNumber = input.nextInt(); // get account number
                    		
                    		Statement st1 = conn.createStatement();
                            ResultSet rs1 = st1.executeQuery("SELECT * FROM account WHERE accountID = "+ acctNumber);
                    		
                            int cnt = 0;
                            while(rs1.next()){
                            	cnt++;
                            	String bal = "" + rs1.getDouble("balance");
                                String type_st = rs1.getString("isAccountType");
                                String bank_st = rs1.getString("bank");

                                System.out.println("\tAccount ID: " + acctNumber + "\tBalance: " + bal + "\tType: " + type_st 
                                + "\tBank: " + bank_st);

                    		}
                            rs1.close();
                            if (cnt <= 0) {
                            	System.out.println("Couldn't find accounts");
                            }
                    		
                    		Statement st6 = conn.createStatement();
                            ResultSet rs6 = st6.executeQuery("SELECT * FROM transaction WHERE accountID = "+ acctNumber +
                            		" AND (SELECT date_part('month', current_date)-1) = (SELECT date_part('month', description))");
                            
                            int count = 0;
                            while(rs6.next()){
                                count++;
                                int transID = rs6.getInt("tID");
                                String amnt = "" + rs6.getDouble("amount");
                                String date = rs6.getDate("description").toString();
                                String type_state = rs6.getString("type");
                                int send = rs6.getInt("accountID");
                                int recieve = rs6.getInt("toAccount");
                                String bank_state = rs6.getString("bank");

                                System.out.println("\tTransaction: " + transID + "\tAmount: " + amnt + "\tDate: " + date 
                                + "\tType: " + type_state  + "\tFrom: " + send  + "\tTo: " + recieve + "\tBank: " + bank_state);

                    		}
                            rs6.close();
                            if (count <= 0) {
                            	System.out.println("Couldn't find any transactions for the month");
                            }
//---------------------------------------------------------------------------------------------------------------------- SHOW PENDING TRANSACTIONS
                    	} else if (mngAction.equals("Pending transactions") && mngPrms.contains("Pending transactions")) {
                    		
                    		System.out.println("What is the # of the account you wish to see the pending transactions of? ");
                            System.out.println("----------------------");
                    		int acctNumber = input.nextInt(); // get account number
                    		
                    		Statement st6 = conn.createStatement();
                            ResultSet rs6 = st6.executeQuery("SELECT * FROM transaction WHERE accountID = "+ acctNumber +
                            		" AND (SELECT date_part('month', current_date)) = (SELECT date_part('month', description))");
                            
                            int count = 0;
                            while(rs6.next()){
                                count++;
                                int transID = rs6.getInt("tID");
                                String amnt = "" + rs6.getDouble("amount");
                                String date = rs6.getDate("description").toString();
                                String type_pend = rs6.getString("type");
                                int send = rs6.getInt("accountID");
                                int recieve = rs6.getInt("toAccount");
                                String bank_pend = rs6.getString("bank");

                                System.out.println("\tTransaction: " + transID + "\tAmount: " + amnt + "\tDate: " + date 
                                + "\tType: " + type_pend  + "\tFrom: " + send  + "\tTo: " + recieve + "\tBank: " + bank_pend);

                    		}
                            rs6.close();
                            if (count <= 0) {
                            	System.out.println("Couldn't find any transactions for the month");
                            }
                    		
//---------------------------------------------------------------------------------------------------------------------- ADD INTEREST
                    	} else if (mngAction.equals("Add interest") && mngPrms.contains("Add interest")) {
                    		System.out.println("These are the current rates for the banks: ");
                    		
                    		Statement st6 = conn.createStatement();
                            ResultSet rs6 = st6.executeQuery("SELECT * FROM account_type");
                            
                            int count = 0;
                            while(rs6.next()){
                                count++;
                                String bankType = rs6.getString("type");
                                String minBal = "" + rs6.getDouble("minimum_balance");
                                String oldInt = "" + rs6.getDouble("interest");
                                String fee = "" + rs6.getDouble("fee");

                                System.out.println("\tType: " + bankType + "\tMinimum Balance: " + minBal + "\tInterest: " + oldInt 
                                + "\tFee: " + fee);
                    		}
                            rs6.close();
                    		
                    		//User selects bank to change
                    		System.out.println("Which account type would you like to change the interest rate of? (Checking / Savings) ");
                            System.out.println("----------------------");
                    		String desBank = input.nextLine();
                    		
                    		System.out.println("What is the new interst rate you'd like to use? (Enter as: 0.XXX) ");
                            System.out.println("----------------------");
                    		double newInterest = input.nextDouble();
                    		
                    		Statement st4 = conn.createStatement();                        		
                    		st4.executeUpdate("UPDATE account_type SET interest = " + newInterest + " WHERE type = '" + desBank + "'");
                    		
                    		System.out.println("Interest Updated");
//---------------------------------------------------------------------------------------------------------------------- ADD OVERDRAFT FEES
                    	} else if (mngAction.equals("Add overdraft fees") && mngPrms.contains("Add overdraft fees")) {
                    		System.out.println("These are the current fees for the following banks: ");
                    		
                    		Statement st6 = conn.createStatement();
                            ResultSet rs6 = st6.executeQuery("SELECT * FROM account_type");
                            
                            int count = 0;
                            while(rs6.next()){
                                count++;
                                String bankType = rs6.getString("type");
                                String minBal = "" + rs6.getDouble("minimum_balance");
                                String oldInt = "" + rs6.getDouble("interest");
                                String fee = "" + rs6.getDouble("fee");

                                System.out.println("\tType: " + bankType + "\tMinimum Balance: " + minBal + "\tInterest: " + oldInt 
                                + "\tFee: " + fee);
                    		}
                            rs6.close();
                    		
                            //User selects bank to change
                    		System.out.println("Which account type would you like to change the fees of? (Checking / Savings) ");
                            System.out.println("----------------------");
                    		String desBank = input.nextLine();
                    		
                    		System.out.println("What is the new interst rate you'd like to use? (Enter as: XXX.XX) ");
                            System.out.println("----------------------");
                    		double newFee = input.nextDouble();
                    		
                    		Statement st4 = conn.createStatement();                        		
                    		st4.executeUpdate("UPDATE account_type SET fee = " + newFee + " WHERE type = '" + desBank + "'");
                    		
                    		System.out.println("Fee Updated");
                    	}
                    }
//---------------------------------------------------------------------------------------------------------------------- ANALYTICS                       
	                else if(action.equals("Analytics") && perms.contains("Analytics")){
	                    System.out.println("What analytic would you like to see?: Total Net Worth, Total Bank Accounts, Total Customers");
	                    System.out.println("----------------------");
	                    String anyl = input.nextLine();
	                    if(anyl.equals("Total Net Worth")){
	                        Statement st6 = conn.createStatement();
	                        ResultSet rs6 = st6.executeQuery("SELECT SUM(balance) FROM account WHERE bank = '" + bank + "'"); 
	                        rs6.next();
	                        String bal = rs6.getString(1);
	                        System.out.println("Net Worth of " + bank + " is " + bal);
	                    } else if(anyl.equals("Total Bank Accounts")){
	                        Statement st6 = conn.createStatement();
	                        ResultSet rs6 = st6.executeQuery("SELECT count(*) FROM account WHERE bank = '" + bank + "'"); 
	                        rs6.next();
	                        String tAccts = rs6.getString(1);
	                        System.out.println("Total amount of accounts at " + bank + " is " + tAccts);
	                    } else if(anyl.equals("Total Customers")){
	                        Statement st6 = conn.createStatement();
	                        ResultSet rs6 = st6.executeQuery("SELECT count(*) FROM (SELECT DISTINCT customerID FROM account WHERE bank = '" + bank + "') a") ;; 
	                        rs6.next();
	                        String tCmrs = rs6.getString(1);
	                        System.out.println("Total amount of customers at " + bank + " is " + tCmrs);
	                    } else {
	                        System.out.println("Not an option!");
	                    }
//---------------------------------------------------------------------------------------------------------------------- LOGOUT
                    } else if(action.equals("Logout")){
                        System.out.println("Bye Bye!");
                        break;
 //---------------------------------------------------------------------------------------------------------------------- UNKNOWN REQUEST
                    } else {
                        System.out.println("Unknown Request");
                    }
                }
                rs.close(); rs2.close(); st.close(); st2.close(); input.close(); 
            } else {
                System.out.println("Failed to make connection!");
            }
        } catch (SQLException e) { System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());} catch (Exception e) {e.printStackTrace();}
      }
}
