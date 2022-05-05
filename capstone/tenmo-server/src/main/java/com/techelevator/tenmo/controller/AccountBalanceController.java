package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountBalanceDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.AccountBalance;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("account_balance")
@PreAuthorize("isAuthenticated()")

public class AccountBalanceController {

    AccountBalanceDao accountBalanceDao;
    UserDao userDao;

    @Autowired
    public AccountBalanceController(AccountBalanceDao accountBalanceDao, UserDao userDao){

        this.accountBalanceDao = accountBalanceDao;
        this.userDao = userDao;
    }

    @GetMapping
    public AccountBalance getFindByUserId(Principal principal){

        String userName = principal.getName();
        Long userId = (long) userDao.findIdByUsername(userName);

        return accountBalanceDao.findByUserId(userId);
    }
}
