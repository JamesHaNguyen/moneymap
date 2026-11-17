package com.moneymap.service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;

import com.moneymap.model.CategoryRule;
import com.moneymap.repository.CategoryRuleRepository;

@Service
public class CategoryService {

    private static final String FALLBACK = "Uncategorized";

    private final CategoryRuleRepository repository;

    public CategoryService(CategoryRuleRepository repository) {
        this.repository = repository;
    }

    public List<CategoryRule> loadRules() {
        return repository.findAll();
    }

    public String categorize(String description, List<CategoryRule> rules) {
        String text = description.toUpperCase(Locale.ROOT);
        return rules.stream()
                .filter(rule -> text.contains(rule.keyword()))
                .max(Comparator.comparingInt(rule -> rule.keyword().length()))
                .map(CategoryRule::category)
                .orElse(FALLBACK);
    }
}