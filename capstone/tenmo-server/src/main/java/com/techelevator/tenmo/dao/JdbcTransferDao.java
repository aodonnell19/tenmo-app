package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountBalance;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao {

    private JdbcTemplate jdbcTemplate;
    private final int receiveTypeId = 1;
    private final int sendTypeId = 2;
    private final int statusTypeIdPending = 1;
    private final int statusTypeIdApproved = 2;
    private final int statusTypeIdRejected = 3;
    private AccountBalanceDao accountBalanceDao;

    @Autowired
    public JdbcTransferDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Transactional
    @Override
    public void transfer(Transfer transfer) {

        String sql = "INSERT INTO transfer (transfer_type_id, transfer_status_id, account_from, account_to, amount)\n" +
                "VALUES (?,?,?,?,?);";

        jdbcTemplate.update(sql, sendTypeId, statusTypeIdApproved, transfer.getAccountFrom(), transfer.getAccountTo(), transfer.getAmount());

        String sql1 = "UPDATE account\n" +
                "SET balance = balance - ?\n" +
                "WHERE account_id = ?;";

        jdbcTemplate.update(sql1, transfer.getAmount(), transfer.getAccountFrom());

        String sql2 = "UPDATE account\n" +
                "SET balance = balance + ?\n" +
                "WHERE account_id = ?;";

        jdbcTemplate.update(sql2, transfer.getAmount(), transfer.getAccountTo());
    }

    @Override
    public List<Transfer> findByUserId(Long userId, Long accountId) {

        List<Transfer> transfers = new ArrayList<>();

        String sql = "SELECT t.transfer_id\n" +
                "     , t.transfer_type_id\n" +
                "     , t.transfer_status_id\n" +
                "     , t.account_from\n" +
                "     , t.account_to\n" +
                "     , t.amount\n" +
                "     , ab.user_id AS user_from\n" +
                "     , ac.user_id AS user_to\n" +
                "FROM transfer as t\n" +
                "         JOIN account as ab\n" +
                "              ON ab.account_id = t.account_from\n" +
                "         JOIN account as ac\n" +
                "              ON ac.account_id = t.account_to\n" +
                "WHERE ab.user_id = ?\n" +
                "   OR ac.user_id = ?;";

        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while (rows.next()){
            Transfer transfer = mapRowToTransfer(rows);
            transfers.add(transfer);
        }

        return transfers;
    }

    private Transfer mapRowToTransfer(SqlRowSet rows) {

        Transfer transfer;

        Long transferId = rows.getLong("transfer_id");
        Long transferTypeId = rows.getLong("transfer_type_id");
        Long transferStatusId = rows.getLong("transfer_status_id");
        Long accountFrom = rows.getLong("account_from");
        Long accountTo = rows.getLong("account_to");
        BigDecimal amount = rows.getBigDecimal("amount");
        Long userFrom = rows.getLong("user_from");
        Long userTo = rows.getLong("user_to");

        transfer = new Transfer(transferId, transferTypeId, transferStatusId, accountFrom, accountTo, amount, userFrom, userTo);

        return transfer;
    }
}
