package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.dto.Transfer;
import org.junit.Assert;
import org.junit.Test;

public class TransferServiceTest {


    @Test
    public void getTransferDetails_whenTransferIdIsNotValid_returnsNull() {

        Long transferIdInvalid = Long.valueOf(1);

        TransferService transferService = new TransferService();

        Transfer actualValue = transferService.getTransferDetails(transferIdInvalid);

        String message = "Invalid transfer ID must return null.";
        Assert.assertNull(message, actualValue);

    }

    @Test
    public void parseTransferUserNames_whenNewTransferAndNewUserAreNull_returnComma() {

        Transfer transfer = null;
        User user = null;

        TransferService transferService = new TransferService();

        String actualValue = transferService.parseTransferUserNames(transfer, user);

        String message = "If transfer and user are null, null pointer exception should be thrown.";

        Assert.assertNull(message, actualValue);
    }
}
