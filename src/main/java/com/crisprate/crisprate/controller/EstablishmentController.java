package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.*;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import com.crisprate.crisprate.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class EstablishmentController {

    private static final Logger log = LoggerFactory.getLogger(EstablishmentController.class);
    private final EstablishmentService establishmentService;
    private final RatingService ratingService;
    private final UserService userService;

    public EstablishmentController(EstablishmentService establishmentService, RatingService ratingService, UserService userService) {
        this.establishmentService = establishmentService;
        this.ratingService = ratingService;
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Establishment> results;

        if (name != null && !name.isBlank()) {
            results = establishmentService.searchByName(name, pageable);
        } else if (city != null && !city.isBlank()) {
            results = establishmentService.searchByCity(city, pageable);
        } else {
            results = establishmentService.getAll(pageable);
        }

        model.addAttribute("establishments", results.getContent());
        model.addAttribute("scores", scoresFor(results.getContent()));
        model.addAttribute("searchName", name);
        model.addAttribute("searchCity", city);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", results.getTotalPages());

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



    @GetMapping("/establishments/new")
    public String newEstablishmentForm() {
        return "establishment-form";
    }

    @PostMapping("/seed")
    public String seed(){
        establishmentService.seedDemoData();
        return "redirect:/";
    }

    @PostMapping("/establishments")
    public String createEstablishment(
            @RequestParam String type,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam(required = false) String chainName,
            Model model) {

        model.addAttribute("type", type);
        model.addAttribute("name", name);
        model.addAttribute("address", address);
        model.addAttribute("city", city);
        model.addAttribute("chainName", chainName);

        if (name.isBlank() || address.isBlank() || city.isBlank()) {
            model.addAttribute("error", "Name, address, and city are all required.");
            return "establishment-form";
        }

        Establishment establishment;

        if ("FAST_FOOD_CHAIN".equals(type)) {
            if (chainName == null || chainName.isBlank()) {
                model.addAttribute("error", "Chain name is required for a fast food chain.");
                return "establishment-form";
            }
            establishment = new FastFoodChain(name, address, city, chainName);
        } else {
            establishment = new Restaurant(name, address, city);
        }

        Establishment saved = establishmentService.save(establishment);
        return "redirect:/establishments/" + saved.getId();
    }

    @PostMapping("/establishment")


    private java.util.Map<Long, Double> scoresFor(List<Establishment> establishments) {
        java.util.Map<Long, Double> scores = new java.util.HashMap<>();
        for (Establishment e : establishments) {
            scores.put(e.getId(), establishmentService.getScoreFor(e));
        }
        return scores;
    }

}
