package com.crisprate.crisprate.entity;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RESTAURANT")
public class Restaurant extends Establishment {

    protected Restaurant() {

    }

    public Restaurant(String name, String address, String city) {
        super(name, address, city);
    }

    @Override
    public double calculateAverageScore() {
        if(getRatings().isEmpty()) {
            return 0.0;
        }
        return getRatings().stream()
                .mapToInt(rating -> rating.getScore())
                .average()
                .orElse(0.0);
    }
}
