package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountBalance;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    void transfer(Transfer transfer);

    List<Transfer> findByUserId(Long userId, Long accountId);
}
