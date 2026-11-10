package com.moneymap.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moneymap.model.Transaction;
import com.moneymap.repository.TransactionRepository;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public record NewTransactionRequest(LocalDate date, String description, BigDecimal amount) {
    }

    private final TransactionRepository repository;

    public TransactionController(TransactionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Transaction> list() {
        return repository.findAll();
    }

        @PostMapping
    public ResponseEntity<?> create(@RequestBody NewTransactionRequest request) {
        if (request.date() == null || request.amount() == null
                || request.description() == null || request.description().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "date, description and amount are required"));
        }
        Transaction toSave = new Transaction(null, request.date(), request.description().trim(),
                request.amount(), null, "MANUAL");
        long id = repository.insert(toSave);
        Transaction saved = new Transaction(id, toSave.date(), toSave.description(),
                toSave.amount(), toSave.category(), toSave.source());
        return ResponseEntity.status(201).body(saved);
    }
}