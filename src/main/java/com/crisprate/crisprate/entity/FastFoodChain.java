package com.crisprate.crisprate.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

@Entity
@DiscriminatorValue("FAST_FOOD_CHAIN")
public class FastFoodChain extends Establishment {

    @Column
    private String chainName;

    @Transient
    private Double chainAverageScore;

    protected FastFoodChain() {
        super();
    }

    public FastFoodChain(String name, String address, String city, String chainName) {
        super(name, address, city);
        this.chainName = chainName;
    }

    public String getChainName() {
        return chainName;
    }

    public void setChainName(String chainName) {
        this.chainName = chainName;
    }

    public void setChainAverageScore(Double chainAverageScore) {
        this.chainAverageScore = chainAverageScore;
    }

    @Override
    public double calculateAverageScore() {
        if (chainAverageScore != null) {
            return chainAverageScore;
        }
        if (getRatings().isEmpty()) {
            return 0.0;
        }
        return getRatings().stream()
                .mapToInt(rating -> rating.getScore())
                .average()
                .orElse(0.0);
    }
}
