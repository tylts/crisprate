package com.crisprate.crisprate.dto;

public class ReportRow {

    private final String name;
    private final String city;
    private final double averageScore;
    private final int ratingCount;

    public ReportRow(String name, String city, double averageScore, int ratingCount) {
        this.name = name;
        this.city = city;
        this.averageScore = averageScore;
        this.ratingCount = ratingCount;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public int getRatingCount() {
        return ratingCount;
    }
}
