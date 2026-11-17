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
import com.moneymap.service.TransactionService;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    public record NewTransactionRequest(LocalDate date, String description, BigDecimal amount) {
    }

    private final TransactionRepository repository;
    private final TransactionService service;

    public TransactionController(TransactionRepository repository, TransactionService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    public List<Transaction> list() {
        return repository.findAll();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewTransactionRequest request) {
        String problem = service.validate(request.date(), request.description(), request.amount());
        if (problem != null) {
            return ResponseEntity.badRequest().body(Map.of("error", problem));
        }
        Transaction saved = service.create(request.date(), request.description(), request.amount());

        return ResponseEntity.status(201).body(saved);
    }
}