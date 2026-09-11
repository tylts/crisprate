package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import com.crisprate.crisprate.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
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

}
