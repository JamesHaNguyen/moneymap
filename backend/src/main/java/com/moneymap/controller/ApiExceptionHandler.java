package com.moneymap.controller;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.moneymap.service.TransactionService.ImportResult;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler({IOException.class, UncheckedIOException.class})
    public ResponseEntity<ImportResult> handleUnreadableFile(Exception e) {
        return ResponseEntity.badRequest()
                .body(new ImportResult(0, List.of("The file could not be read as CSV")));
    }
}