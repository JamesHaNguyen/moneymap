package com.moneymap.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.moneymap.model.Transaction;

public class BaselineDetector {

    public record Anomaly(long transactionId, double score) {
    }

    private static final int MIN_HISTORY = 8;
    private static final double THRESHOLD = 3.5;
    private static final double MAD_SCALE = 0.6745;

    public List<Anomaly> detect(List<Transaction> transactions) {
        Map<String, List<Transaction>> byCategory = transactions.stream()
                .filter(t -> t.amount().signum() < 0 && t.category() != null)
                .collect(Collectors.groupingBy(Transaction::category));

        List<Anomaly> anomalies = new ArrayList<>();
        for (List<Transaction> group : byCategory.values()) {
            if (group.size() < MIN_HISTORY) {
                continue;
            }
            double[] spend = group.stream()
                    .mapToDouble(t -> t.amount().abs().doubleValue())
                    .toArray();
            double median = median(spend);
            double mad = median(Arrays.stream(spend).map(x -> Math.abs(x - median)).toArray());
            if (mad == 0) {
                continue;
            }
            for (Transaction t : group) {
                double score = MAD_SCALE * (t.amount().abs().doubleValue() - median) / mad;
                if (score > THRESHOLD) {
                    anomalies.add(new Anomaly(t.id(), score));
                }
            }
        }
        return anomalies;
    }
    private static double median(double[] values) {
        double[] sorted = values.clone();
        Arrays.sort(sorted);
        int mid = sorted.length / 2;
        return sorted.length % 2 == 0 ? (sorted[mid - 1] + sorted[mid]) / 2 : sorted[mid];
    }
}