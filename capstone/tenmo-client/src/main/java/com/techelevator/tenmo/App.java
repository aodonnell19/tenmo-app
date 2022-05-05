package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.model.dto.AccountBalance;
import com.techelevator.tenmo.model.dto.Transfer;
import com.techelevator.tenmo.services.AccountBalanceService;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TransferService;

import java.math.BigDecimal;
import java.util.List;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);
    private final AccountBalanceService accountBalanceService = new AccountBalanceService();
    private final TransferService transferService = new TransferService();

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
		// TODO Auto-generated method stub
        AccountBalance accountBalance = accountBalanceService.getAccountBalanceForCurrentUser(currentUser.getToken());

        System.out.println("\nYour current account balance is: " + "$" + accountBalance.getBalance());
		
	}

	private void viewTransferHistory() {

        consoleService.printTransferHistoryHeader();

        transferService.setAuthToken(currentUser.getToken());
        AccountBalance accountBalance = accountBalanceService.getAccountBalanceForCurrentUser(currentUser.getToken());
        List<Transfer> transferHistory = transferService.getTransferHistory();
        List<User> registeredUsers = transferService.getListOfRegisteredUsers();
        consoleService.printTransferHistory(transferHistory, registeredUsers, accountBalance);
        viewTransferDetails();
	}

    private void viewTransferDetails() {
        int userChoice = 1;
        while(userChoice == 1){
            Long transferId = (long)consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
            if(transferId.equals((long)0)) return;

            consoleService.printTransferDetailsHeader();
            Transfer transfer = transferService.getTransferDetails(transferId);
            if (transfer == null) {
                consoleService.printErrorMessage();
                return;
            }
            String[] userNames = transferService.parseTransferUserNames(transfer, currentUser.getUser()).split(",");
            String userNameFrom = userNames[0];
            String userNameTo = userNames[1];

            consoleService.printTransferDetails(transfer, userNameFrom, userNameTo);

            userChoice = consoleService.promptForInt("Press 1 to view another transfer or press 0 to exit: ");
            if (userChoice == 1) viewTransferHistory();
        }
    }


    private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
		// TODO Auto-generated method stub
        consoleService.printUsersHeader();

        transferService.setAuthToken(currentUser.getToken());
        List<User> registeredUsers = transferService.getListOfRegisteredUsers();
        registeredUsers.forEach((user) -> {
            System.out.print(user.getId() + "\t");
            System.out.println(user.getUsername());
        });
        System.out.println("---------");
        System.out.println();
        Long receiverId = (long)consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        if(receiverId.equals((long)0)) return;
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        Transfer transfer = new Transfer(currentUser.getUser().getId(), receiverId, amount);

        BigDecimal balanceOfCurrentUser = accountBalanceService.getAccountBalanceForCurrentUser(currentUser.getToken()).getBalance();

        if (amount.compareTo(balanceOfCurrentUser) <= 0 && amount.compareTo(BigDecimal.valueOf(0)) > 0){

            transferService.createTransfer(transfer);
        }
        else {
            System.out.println("Transfer failed. Please try again.");
        }
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}



}
