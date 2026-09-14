package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final EstablishmentService establishmentService;
    private final RatingService ratingService;

    public AdminController(EstablishmentService establishmentService, RatingService ratingService) {
        this.establishmentService = establishmentService;
        this.ratingService = ratingService;
    }

    @GetMapping
    public String dashboard(Model model) {
        List<Establishment> establishments = establishmentService.getAll();

        List<Rating> allRatings = new ArrayList<>();
        for (Establishment e : establishments) {
            allRatings.addAll(ratingService.getRatingsForEstablishment(e.getId()));
        }

        model.addAttribute("establishments", establishments);
        model.addAttribute("ratings", allRatings);
        return "admin";
    }

    @PostMapping("/ratings/{id}/delete")
    public String deleteRating(@PathVariable Long id) {
        ratingService.adminDeleteRating(id);
        return "redirect:/admin";
    }

    @PostMapping("/establishments/{id}/delete")
    public String deleteEstablishment(@PathVariable Long id) {
        establishmentService.delete(id);
        return "redirect:/admin";
    }



}
