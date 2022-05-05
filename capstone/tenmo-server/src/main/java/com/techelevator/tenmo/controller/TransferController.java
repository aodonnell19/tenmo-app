package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountBalanceDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.AccountBalance;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("transfer")
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private UserDao userDao;
    private AccountBalanceDao accountBalanceDao;
    private TransferDao transferDao;

    @Autowired
    public TransferController(UserDao userDao, TransferDao transferDao, AccountBalanceDao accountBalanceDao){
        this.userDao = userDao;
        this.transferDao = transferDao;
        this.accountBalanceDao = accountBalanceDao;
    }

    @GetMapping
    public List<User> getListOfRegisteredUsers(Principal principal){
        String userName = principal.getName();
        Long userId = (long) userDao.findIdByUsername(userName);

        List<User> newList = userDao.findAll();

        newList.removeIf(user -> Objects.equals(user.getId(), userId));

        return newList;
    }

    @PutMapping
    public void createTransfer(@RequestBody Transfer transfer){

        AccountBalance accountBalanceFrom = accountBalanceDao.findByUserId(transfer.getUserFrom());
        transfer.setAccountFrom(accountBalanceFrom.getAccountId());

        AccountBalance accountBalanceTo = accountBalanceDao.findByUserId(transfer.getUserTo());
        transfer.setAccountTo(accountBalanceTo.getAccountId());

        transferDao.transfer(transfer);

    }

    @GetMapping(value = "/history")
    public List<Transfer> getListOfTransfers(Principal principal){
        String userName = principal.getName();
        Long userId = (long) userDao.findIdByUsername(userName);
        AccountBalance accountBalance = accountBalanceDao.findByUserId(userId);
        Long accountId = accountBalance.getAccountId();
        List<Transfer> transfers = transferDao.findByUserId(userId, accountId);

        return transfers;
    }
}
