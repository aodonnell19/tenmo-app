package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountBalance;

public interface AccountBalanceDao {

    AccountBalance findByUserId(Long userId);

}
