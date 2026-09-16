package com.crisprate.crisprate.controller;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.FastFoodChain;
import com.crisprate.crisprate.entity.Rating;
import com.crisprate.crisprate.service.EstablishmentService;
import com.crisprate.crisprate.service.RatingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/establishments/{id}/edit")
    public String editEstablishmentForm(
            @PathVariable Long id,
            Model model) {
        model.addAttribute("establishment", establishmentService.getById(id));
        return "admin-establishment-edit";
    }

    @PostMapping("/establishments/{id}/edit")
    public String updateEstablishment(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String city,
            @RequestParam(required = false) String chainName,
            Model model) {

        Establishment establishment = establishmentService.getById(id);

        if (name.isBlank() || address.isBlank() || city.isBlank()) {
            model.addAttribute("error", "Name, address, and city are all required.");
            model.addAttribute("establishment", establishment);
            return "admin-establishment-edit";
        }

        if (establishment instanceof FastFoodChain && (chainName == null || chainName.isBlank())) {
            model.addAttribute("error", "The name of the restaurant chain is required.");
            model.addAttribute("establishment", establishment);
            return "admin-establishment-edit";
        }

        establishmentService.update(id, name, address, city, chainName);
        return "redirect:/admin";
    }




}
