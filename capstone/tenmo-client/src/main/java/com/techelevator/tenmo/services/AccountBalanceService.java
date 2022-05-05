package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.User;
import io.cucumber.java.bs.A;
import io.cucumber.java.en_old.Ac;
import okhttp3.Response;
import org.springframework.http.*;

import com.techelevator.tenmo.model.dto.AccountBalance;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

public class AccountBalanceService {

    private static final String BASE_URL = "http://localhost:8080/";
    private static final RestTemplate restTemplate = new RestTemplate();

    private String authToken = null;

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }


    public AccountBalance getAccountBalanceForCurrentUser(String authToken){

        AccountBalance accountBalance = new AccountBalance();

        try {
            String url = BASE_URL + "account_balance";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(authToken);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<AccountBalance> response = restTemplate.exchange(url
                                                    , HttpMethod.GET
                                                    , entity
                                                    , AccountBalance.class);

            accountBalance = response.getBody();
        }
        catch (RestClientResponseException e){

        }
        return accountBalance;
    }

}
