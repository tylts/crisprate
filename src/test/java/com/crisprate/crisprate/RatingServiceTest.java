package com.crisprate.crisprate;

import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.Restaurant;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.repository.RatingRepository;
import com.crisprate.crisprate.service.RatingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @InjectMocks
    private RatingService ratingService;

    @Test
    void submitRating_throwsException_whenScoreTooLow() {
        User user = new User("testuser", "test@example.com", "password", User.Role.USER);
        Restaurant restaurant = new Restaurant("Test Diner", "123 Main St", "Fort Worth");

        assertThrows(IllegalArgumentException.class, () ->
                ratingService.submitRating(user, restaurant, 0, "Bad score"));
    }

    @Test
    void deleteRating_throwsSecurityException_whenNotOwner() {
        User owner = new User("owner", "owner@example.com", "pass", User.Role.USER);
        User intruder = new User("intruder", "intruder@example.com", "pass", User.Role.USER);
        setID(owner, 1L);
        setID(intruder, 2L);

        Restaurant restaurant = new Restaurant("Test Diner", "123 Main St", "Fort Worth");
        Rating rating = new Rating(4, "good", owner, restaurant);
        setID(rating, 100L);

        when(ratingRepository.findById(100L)).thenReturn(Optional.of(rating));

        assertThrows(SecurityException.class, () ->
                ratingService.deleteRating(100L, intruder));
    }

    @Test
    void deleteRating_succeeds_whenRequesterIsOwner() {
        User owner = new User("owner", "owner@example.com", "password", User.Role.USER);
        setID(owner, 1L);

        Restaurant restaurant = new Restaurant("Test Diner", "123 Main St", "Fort Worth");
        Rating rating = new Rating(4, "good", owner, restaurant);
        setID(rating, 100L);

        when(ratingRepository.findById(100L)).thenReturn(Optional.of(rating));

        ratingService.deleteRating(100L, owner);

        verify(ratingRepository).delete(rating);
    }

    private void setID(Object entity, Long id) {
        try {
            var field = entity.getClass().getDeclaredField("id");
            field.setAccessible(true);
            field.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
