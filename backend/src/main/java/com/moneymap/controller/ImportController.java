package com.moneymap.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.moneymap.service.TransactionService;
import com.moneymap.service.TransactionService.ImportResult;

@RestController
@RequestMapping("/api/import")
public class ImportController {

    private final TransactionService service;

    public ImportController(TransactionService service) {
        this.service = service;
    }

    @PostMapping("/csv")
    public ResponseEntity<ImportResult> importCsv(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ImportResult(0, List.of("No file uploaded")));
        }
        ImportResult result = service.importCsv(file.getInputStream());
        if (!result.errors().isEmpty()) {
            return ResponseEntity.badRequest().body(result);
        }
        return ResponseEntity.ok(result);
    }
}