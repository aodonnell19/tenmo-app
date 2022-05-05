package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.AccountBalance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Component
public class JbdcAccountBalanceDao implements AccountBalanceDao{

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JbdcAccountBalanceDao(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public AccountBalance findByUserId(Long userId) {

        AccountBalance accountBalance = null;

        String sql = "SELECT account_id\n" +
                     "    , user_id\n" +
                     "    , balance\n" +
                     "FROM account\n" +
                     "WHERE user_id = ?;";

        SqlRowSet rows = jdbcTemplate.queryForRowSet(sql, userId);

        if (rows.next()){
            accountBalance = mapRowToAccountBalance(rows);
        }

        return accountBalance;
    }


    private AccountBalance mapRowToAccountBalance(SqlRowSet row){

        AccountBalance accountBalance;

        Long accountId = row.getLong("account_id");
        Long userId = row.getLong("user_id");
        BigDecimal balance = row.getBigDecimal("balance");

        accountBalance = new AccountBalance(accountId, userId, balance);

        return accountBalance;
    }
}
