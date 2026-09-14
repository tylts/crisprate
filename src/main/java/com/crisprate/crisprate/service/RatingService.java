package com.crisprate.crisprate.service;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.repository.RatingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating submitRating(User user, Establishment establishment, int score, String comment) {
        if (score < 1 || score > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5.");
        }
        Rating rating = new Rating(score, comment, user, establishment);
        establishment.addRating(rating);
        return ratingRepository.save(rating);
    }

    public Rating updateRating(Long ratingId, User requestingUser, int newScore, String newComment) {
        if (newScore < 1 || newScore > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5.");
        }
        Rating rating = getById(ratingId);
        assertOwnership(rating, requestingUser);
        rating.setScore(newScore);
        rating.setComment(newComment);
        return ratingRepository.save(rating);
    }

    public void deleteRating(Long ratingId, User requestingUser) {
        Rating rating = getById(ratingId);
        assertOwnership(rating, requestingUser);
        ratingRepository.delete(rating);
    }

    public void adminDeleteRating(Long ratingId) {
        Rating rating = getById(ratingId);
        ratingRepository.delete(rating);
    }

    public List<Rating> getRatingsForEstablishment(Long establishmentId) {
        return ratingRepository.findByEstablishmentIdOrderByCreatedAtDesc(establishmentId);
    }

    public List<Rating> getRatingsForUser(Long userId) {
        return ratingRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public Rating getById(Long ratingId) {
        return ratingRepository.findById(ratingId)
                .orElseThrow(() -> new NoSuchElementException("Rating not found with id: " + ratingId));
    }

    private void assertOwnership(Rating rating, User requestingUser) {
        if (!rating.getUser().getId().equals(requestingUser.getId())) {
            throw new SecurityException("You do not have permission to modify this rating.");
        }
    }


}
