package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import com.crisprate.crisprate.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RatingController {

    private final RatingService ratingService;
    private final EstablishmentService establishmentService;
    private final UserService userService;

    public RatingController(RatingService ratingService,
                            EstablishmentService establishmentService,
                            UserService userService) {
        this.ratingService = ratingService;
        this.establishmentService = establishmentService;
        this.userService = userService;
    }

    @PostMapping("/establishments/{id}/ratings")
    public String submitRating(
            @PathVariable Long id,
            @RequestParam int score,
            @RequestParam(required = false) String comment,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User user = userService.getByUsername(authentication.getName());
        Establishment establishment = establishmentService.getById(id);

        try {
            ratingService.submitRating(user, establishment, score, comment);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("ratingError", e.getMessage());
        }

        return "redirect:/establishments/" + id;
    }

    @PostMapping("/ratings/{id}/delete")
    public String deleteRating(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        User user = userService.getByUsername(authentication.getName());
        Long establishmentId = ratingService.getById(id).getEstablishment().getId();

        try {
            ratingService.deleteRating(id, user);
        } catch (SecurityException e) {
            redirectAttributes.addFlashAttribute("ratingError", e.getMessage());
        }

        return "redirect:/establishments/" + establishmentId;
    }

    @GetMapping("ratings/{id}/edit")
    public String editRatingForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {
        Rating rating = ratingService.getById(id);

        if (!rating.getUser().getUsername().equals(authentication.getName())) {
            throw new SecurityException("You do not have permission to edit this rating.");
        }

        model.addAttribute("rating", rating);
        return "rating-edit";
    }

    @PostMapping("/ratings/{id}/edit")
    public String updateRating(
            @PathVariable Long id,
            @RequestParam int score,
            @RequestParam(required = false) String comment,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        User user = userService.getByUsername(authentication.getName());
        Long establishmentId = ratingService.getById(id).getEstablishment().getId();

        try {
            ratingService.updateRating(id, user, score, comment);
        } catch (SecurityException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("ratingError", e.getMessage());
        }

        return "redirect:/establishments/" + establishmentId;
    }



}
