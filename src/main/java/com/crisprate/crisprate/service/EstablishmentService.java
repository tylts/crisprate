package com.crisprate.crisprate.service;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.FastFoodChain;
import com.crisprate.crisprate.entity.User;
import com.crisprate.crisprate.repository.EstablishmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;

    public EstablishmentService(EstablishmentRepository establishmentRepository) {
        this.establishmentRepository = establishmentRepository;
    }

    public Establishment save(Establishment establishment) {
        return establishmentRepository.save(establishment);
    }

    public Establishment getById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Establishment not found with id: " + id));
    }

    public List<Establishment> getAll() {
        return establishmentRepository.findAll();
    }

    public List<Establishment> searchByName(String name) {
        return establishmentRepository.findByNameContainingIgnoreCase(name);
    }

    public List<Establishment> searchByCity(String city) {
        return establishmentRepository.findByCityContainingIgnoreCase(city);
    }

    public void delete(Long id) {
        establishmentRepository.deleteById(id);
    }

    // computes each establishment's score, resolving chain-wide averages for FastFoodChain
    public double getScoreFor(Establishment establishment) {
        if (establishment instanceof FastFoodChain chain) {
            resolveChainAverage(chain);
        }
        return establishment.calculateAverageScore();
    }

    private void resolveChainAverage(FastFoodChain chain) {
        List<FastFoodChain> siblings = establishmentRepository.findAllByChainName(chain.getChainName());

        double total = 0;
        int count = 0;
        for (FastFoodChain sibling : siblings) {
            for (var rating : sibling.getRatings()) {
                total += rating.getScore();
                count++;
            }
        }

        double average = count == 0 ? 0.0 : total / count;
        chain.setChainAverageScore(average);
    }

    public Establishment update(Long id, String name, String address, String city, String chainName) {

        Establishment establishment = getById(id);
        establishment.setName(name);
        establishment.setAddress(address);
        establishment.setCity(city);

        if (establishment instanceof FastFoodChain chain) {
            chain.setChainName(chainName);
        }

        return establishmentRepository.save(establishment);
    }

}
