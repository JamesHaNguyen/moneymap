package com.moneymap.repository;

import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.moneymap.model.Transaction;

@Repository
public class TransactionRepository {

    private final JdbcClient jdbc;

    public TransactionRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }
        public List<Transaction> findAll() {
        return jdbc.sql("""
                SELECT id, txn_date, description, amount, category, source
                FROM transactions
                ORDER BY txn_date DESC, id DESC
                """)
                .query((rs, rowNum) -> new Transaction(
                        rs.getLong("id"),
                        rs.getObject("txn_date", LocalDate.class),
                        rs.getString("description"),
                        rs.getBigDecimal("amount"),
                        rs.getString("category"),
                        rs.getString("source")))
                .list();
    }
        public long insert(Transaction t) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO transactions (txn_date, description, amount, category, source)
                VALUES (:date, :description, :amount, :category, :source)
                """)
                .param("date", t.date())
                .param("description", t.description())
                .param("amount", t.amount())
                .param("category", t.category())
                .param("source", t.source())
                .update(keys);
        return keys.getKey().longValue();
    }
}