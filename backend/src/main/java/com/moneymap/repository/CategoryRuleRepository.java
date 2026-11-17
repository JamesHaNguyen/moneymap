package com.moneymap.repository;

import java.util.List;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.moneymap.model.CategoryRule;

@Repository
public class CategoryRuleRepository {

    private final JdbcClient jdbc;

    public CategoryRuleRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    public List<CategoryRule> findAll() {
        return jdbc.sql("SELECT keyword, category FROM category_rules")
                .query((rs, rowNum) -> new CategoryRule(
                        rs.getString("keyword"),
                        rs.getString("category")))
                .list();
    }
}