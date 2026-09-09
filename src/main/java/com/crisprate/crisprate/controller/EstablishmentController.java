package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class EstablishmentController {

    private final EstablishmentService establishmentService;
    private final RatingService ratingService;

    public EstablishmentController(EstablishmentService establishmentService, RatingService ratingService) {
        this.establishmentService = establishmentService;
        this.ratingService = ratingService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            Model model) {

        List<Establishment> results;

        if (name != null && !name.isBlank()) {
            results = establishmentService.searchByName(name);
        } else if (city != null && !city.isBlank()) {
            results = establishmentService.searchByCity(city);
        } else {
            results = establishmentService.getAll();
        }

        model.addAttribute("establishments", results);
        model.addAttribute("scores", scoresFor(results));
        model.addAttribute("searchName", name);
        model.addAttribute("searchCity", city);

        return "home";
    }

    @GetMapping("/establishments/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Establishment establishment = establishmentService.getById(id);
        double score = establishmentService.getScoreFor(establishment);
        List<Rating> ratings = ratingService.getRatingsForEstablishment(id);

        model.addAttribute("establishment", establishment);
        model.addAttribute("score", score);
        model.addAttribute("ratings", ratings);

        return "establishment-detail";
    }

    private java.util.Map<Long, Double> scoresFor(List<Establishment> establishments) {
        java.util.Map<Long, Double> scores = new java.util.HashMap<>();
        for (Establishment e : establishments) {
            scores.put(e.getId(), establishmentService.getScoreFor(e));
        }
        return scores;
    }

}
