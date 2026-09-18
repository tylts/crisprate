package com.crisprate.crisprate;

import com.crisprate.crisprate.entity.FastFoodChain;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.Restaurant;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.repository.EstablishmentRepository;
import com.crisprate.crisprate.service.EstablishmentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EstablishmentServiceTest {

    @Mock
    private EstablishmentRepository establishmentRepository;

    @InjectMocks
    private EstablishmentService establishmentService;

    @Test
    void getScoreFor_restaurant_usesLocalRatingsOnly() {
        Restaurant restaurant= new Restaurant("Taco Diner", "123 Street", "NRH");

        double result = establishmentService.getScoreFor(restaurant);

        assertEquals(0.0, result);
    }

    @Test
    void getScoreFor_fastFoodChain_resolvesChainWideAverage() {
        FastFoodChain locationA = new FastFoodChain("Chain Location A", "1 st", "Fort Worth", "Test Chain");
        FastFoodChain locationB = new FastFoodChain("Chain Location B", "2 st", "Dallas", "Test Chain");
        User user = new User("testuser", "test@example.com", "password", User.Role.USER);

        locationA.addRating(new Rating(5, "Incredible", user, locationA));
        locationA.addRating(new Rating(3, "Fine", user, locationA));
        locationB.addRating(new Rating(1, "Bad", user, locationB));


        when(establishmentRepository.findAllByChainName("Test Chain")).thenReturn(List.of(locationA, locationB));

        double result = establishmentService.getScoreFor(locationA);

        assertEquals(3.0, result);
    }
}
