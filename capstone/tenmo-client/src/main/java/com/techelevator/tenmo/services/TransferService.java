package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.dto.AccountBalance;
import com.techelevator.tenmo.model.dto.Transfer;
import org.springframework.http.*;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TransferService {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public List<User> getListOfRegisteredUsers() {
        List<User> listOfRegisteredUsers = new ArrayList<>();

        try {
            String url = BASE_URL + "transfer";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<User[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class);
            listOfRegisteredUsers = Arrays.asList(response.getBody());
        } catch (RestClientResponseException e) {

        }

        return listOfRegisteredUsers;
    }

    @ResponseStatus(HttpStatus.CREATED)
    public boolean createTransfer(Transfer transfer) {

        try {
            String url = BASE_URL + "transfer";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<Transfer> entity = new HttpEntity<>(transfer, headers);

            restTemplate.put(url, entity, Void.class);
            System.out.println("Approved. Thank you!");
            return true;

        } catch (RestClientResponseException e) {
            System.out.println("Transfer failed. Please try again.");
            return false;
        }
    }

    public List<Transfer> getTransferHistory() {
        List<Transfer> transferHistory = new ArrayList<>();

        try {
            String url = BASE_URL + "transfer/history";
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Transfer[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class);
            transferHistory = Arrays.asList(response.getBody());
        } catch (RestClientResponseException e) {

        }

        return transferHistory;
    }

    public Transfer getTransferDetails(Long transferId) {
        List<Transfer> transferList = getTransferHistory();

        for (Transfer transfer : transferList) {
            if (transfer.getTransferId().equals(transferId)) {
                return transfer;
            }
        }
        return null;
    }

    public String parseTransferUserNames(Transfer transfer, User currentUser) {
        User partner = null;
        String userNameFrom = "";
        String userNameTo = "";
        List<User> registeredUsers = getListOfRegisteredUsers();
        for (User user : registeredUsers) {
            if (user.getId().equals(transfer.getUserTo()) || user.getId().equals(transfer.getUserFrom())) {
                partner = user;
            }
        }
        if (partner.getId().equals(transfer.getUserFrom())) {
            userNameFrom = userNameFrom + partner.getUsername();
        } else {
            userNameTo = userNameTo + partner.getUsername();
        }
        if (userNameFrom.equals("")) {
            userNameFrom = userNameFrom + currentUser.getUsername();
        } else {
            userNameTo = userNameTo + currentUser.getUsername();
        }

        return userNameFrom + "," + userNameTo;
    }
}
