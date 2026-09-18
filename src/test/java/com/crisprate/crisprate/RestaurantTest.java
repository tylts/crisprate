package com.crisprate.crisprate;

import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.Restaurant;
import com.crisprate.crisprate.entity.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestaurantTest {

    @Test
    void calculateAverageScore_returnsZero_whenNoRatings() {
        Restaurant restaurant = new Restaurant("Test Diner", "123 Easy Street", "Watauga");

        double result = restaurant.calculateAverageScore();

        assertEquals(0.0, result);
    }

    @Test
    void calculateAverageScore_averagesOnlyThisLocationsRatings() {
        Restaurant restaurant = new Restaurant("Test Diner", "123 Easy Street", "Watauga");
        User user = new User("testuser", "testuser@example.com", "hashedpw", User.Role.USER);

        restaurant.addRating(new Rating(4, "Good enough", user, restaurant));
        restaurant.addRating(new Rating(2, "Bad", user, restaurant));

        double result = restaurant.calculateAverageScore();

        assertEquals(3.0, result);
    }


}
