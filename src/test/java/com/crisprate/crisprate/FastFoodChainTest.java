package com.crisprate.crisprate;

import com.crisprate.crisprate.entity.FastFoodChain;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FastFoodChainTest {

    @Test
    void calculateAverageScore_fallsBackToLocalRatings_whenChainAverageNoSet() {
        FastFoodChain chain = new FastFoodChain("Taco Bueno - Location", "123 Street", "NRH", "Taco Bueno");
        User user = new User("testuser", "test@example.com", "password", User.Role.USER);

        chain.addRating(new Rating(5, "incredible", user, chain));
        chain.addRating(new Rating(3, "fine", user, chain));

        double result = chain.calculateAverageScore();

        assertEquals(4.0, result);

    }

    @Test
    void calculateAverageScore_usesChainAverage_whenSet() {
        FastFoodChain chain = new FastFoodChain("Taco Bueno - Location", "123 Street", "NRH", "Taco Bueno");
        User user = new User("testuser", "test@example.com", "password", User.Role.USER);

        chain.addRating(new Rating(1, "real bad", user, chain));
        chain.setChainAverageScore(4.5);

        double result = chain.calculateAverageScore();

        assertEquals(4.5, result);
    }

    @Test
    void calculateAverageScore_returnsZero_whenNoRatingsAndNoChainAverageSet() {
        FastFoodChain chain = new FastFoodChain("Taco Bueno - Location", "123 Street", "NRH", "Taco Bueno");

        double result = chain.calculateAverageScore();

        assertEquals(0.0, result);

    }
}
