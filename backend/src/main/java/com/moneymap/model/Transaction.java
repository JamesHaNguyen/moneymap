package com.moneymap.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Transaction(
        Long id,
        LocalDate date,
        String description,
        BigDecimal amount,
        String category,
        String source) {
}