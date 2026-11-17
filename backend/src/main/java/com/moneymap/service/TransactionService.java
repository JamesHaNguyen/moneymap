package com.moneymap.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moneymap.model.CategoryRule;
import com.moneymap.model.Transaction;
import com.moneymap.repository.TransactionRepository;

@Service
public class TransactionService {

    public record ImportResult(int imported, List<String> errors) {
    }

    private final TransactionRepository repository;
    private final CategoryService categories;

    public TransactionService(TransactionRepository repository, CategoryService categories) {
        this.repository = repository;
        this.categories = categories;
    }

    public String validate(LocalDate date, String description, BigDecimal amount) {
        if (date == null) return "date is required";
        if (description == null || description.isBlank()) return "description is required";
        if (amount == null) return "amount is required";
        return null;
    }
    
    @Transactional
    public ImportResult importCsv(InputStream in) throws IOException {
        List<Transaction> rows = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .get();

        List<CategoryRule> rules = categories.loadRules();
        int row = 0;
        try (CSVParser parser = CSVParser.parse(new InputStreamReader(in, StandardCharsets.UTF_8), format)) {
            for (CSVRecord record : parser) {
                row++;
                try {
                    LocalDate date = LocalDate.parse(record.get("date"));
                    String description = record.get("description");
                    BigDecimal amount = new BigDecimal(record.get("amount"));
                    String problem = validate(date, description, amount);
                    if (problem != null) {
                        errors.add("Row " + row + ": " + problem);
                    } else {
                        rows.add(new Transaction(null, date, description, amount,
                                categories.categorize(description, rules), "CSV"));
                    }
                } catch (DateTimeParseException | IllegalArgumentException e) {
                    errors.add("Row " + row + ": invalid value (" + e.getMessage() + ")");
                }
            }
        }
        if (row == 0) {
            errors.add("The file contains no data rows");
        }
        if (!errors.isEmpty()) {
            return new ImportResult(0, errors);
        }
        rows.forEach(repository::insert);
        return new ImportResult(rows.size(), List.of());
    }

        public Transaction create(LocalDate date, String description, BigDecimal amount) {
        String clean = description.trim();
        String category = categories.categorize(clean, categories.loadRules());
        Transaction toSave = new Transaction(null, date, clean, amount, category, "MANUAL");
        long id = repository.insert(toSave);
        return new Transaction(id, date, clean, amount, category, "MANUAL");
    }
}