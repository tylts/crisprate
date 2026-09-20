package com.crisprate.crisprate.service;

import com.crisprate.crisprate.entity.*;
import com.crisprate.crisprate.repository.EstablishmentRepository;
import com.crisprate.crisprate.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EstablishmentService {

    private final EstablishmentRepository establishmentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public EstablishmentService(EstablishmentRepository establishmentRepository,
                                UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {
        this.establishmentRepository = establishmentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Establishment save(Establishment establishment) {
        return establishmentRepository.save(establishment);
    }

    public Establishment getById(Long id) {
        return establishmentRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Establishment not found with id: " + id));
    }

    public Page<Establishment> getAll(Pageable pageable) {
        return establishmentRepository.findAll(pageable);
    }

    public List<Establishment> getAllUnpaged() {
        return establishmentRepository.findAll();
    }

    public List<Establishment> searchByCityUnpaged(String city) {
        return establishmentRepository.findByCityContainingIgnoreCase(city, Pageable.unpaged()).getContent();
    }

    public Page<Establishment> searchByName(String name, Pageable pageable) {
        return establishmentRepository.findByNameContainingIgnoreCase(name, pageable);
    }

    public Page<Establishment> searchByCity(String city, Pageable pageable) {
        return establishmentRepository.findByCityContainingIgnoreCase(city, pageable);
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

    public void seedDemoData() {
        if (!establishmentRepository.findAll().isEmpty()) {
            return;
        }

        User demoUser = userRepository.findByUsername("demo_reviewer")
                .orElseGet(() -> userRepository.save(new User(
                        "demo_reviewer",
                        "demo_reviewer@crisprate.com",
                        passwordEncoder.encode("this-is-not-real"),
                        User.Role.USER)));

        Restaurant r1 = establishmentRepository.save(new Restaurant("The Leaky Faucet", "12 Test St", "Fort Worth"));
        addDemoRating(r1, demoUser, 4, "Surprisingly crisp for a hole-in-the-wall spot.");
        addDemoRating(r1, demoUser, 3, "Decent, went a little flat by the time I finished.");

        Restaurant r2 = establishmentRepository.save(new Restaurant("Grandpa's Waffle House", "34 Test St", "Arlington"));
        addDemoRating(r2, demoUser, 5, "Perfectly carbonated, best in Arlington.");

        Restaurant r3 = establishmentRepository.save(new Restaurant("The Soggy Biscuit", "56 Test St", "Dallas"));
        addDemoRating(r3, demoUser, 2, "Name checks out unfortunately.");
        addDemoRating(r3, demoUser, 1, "Basically flat soda water.");

        Restaurant r4 = establishmentRepository.save(new Restaurant("Nacho Average Diner", "78 Test St", "Hurst"));
        addDemoRating(r4, demoUser, 4, "Solid fizz, good ice-to-soda ratio.");

        Restaurant r5 = establishmentRepository.save(new Restaurant("Flat Tire Cafe", "90 Test St", "Watauga"));
        addDemoRating(r5, demoUser, 2, "Ironically, kind of flat.");

        Restaurant r6 = establishmentRepository.save(new Restaurant("The Crooked Spoon", "11 Test St", "Bedford"));
        addDemoRating(r6, demoUser, 5, "Best Diet Coke I've had all year.");

        Restaurant r7 = establishmentRepository.save(new Restaurant("Sir Loin's Steakhouse", "22 Test St", "Euless"));
        addDemoRating(r7, demoUser, 3, "Average, nothing special.");

        Restaurant r8 = establishmentRepository.save(new Restaurant("The Gravy Train", "33 Test St", "North Richland Hills"));
        addDemoRating(r8, demoUser, 4, "Consistently good every visit.");

        Restaurant r9 = establishmentRepository.save(new Restaurant("Wanda's World Famous Diner", "44 Test St", "Keller"));
        addDemoRating(r9, demoUser, 5, "Lives up to the name.");

        Restaurant r10 = establishmentRepository.save(new Restaurant("The Rusty Skillet", "55 Test St", "Saginaw"));
        addDemoRating(r10, demoUser, 3, "Fine, wouldn't go out of my way.");

        FastFoodChain chikn1 = establishmentRepository.save(new FastFoodChain("Chikn Shak - Location 1", "500 Chain St", "Fort Worth", "Chikn Shak"));
        addDemoRating(chikn1, demoUser, 4, "Reliable, as always.");

        FastFoodChain chikn2 = establishmentRepository.save(new FastFoodChain("Chikn Shak - Location 2", "600 Chain St", "Arlington", "Chikn Shak"));
        addDemoRating(chikn2, demoUser, 3, "A bit watered down today.");

        FastFoodChain chikn3 = establishmentRepository.save(new FastFoodChain("Chikn Shak - Location 3", "700 Chain St", "Dallas", "Chikn Shak"));
        addDemoRating(chikn3, demoUser, 5, "Perfect every time at this location.");

        FastFoodChain taco1 = establishmentRepository.save(new FastFoodChain("Taco Barn - Location 1", "800 Chain St", "Hurst", "Taco Barn"));
        addDemoRating(taco1, demoUser, 2, "Needs a new CO2 tank.");

        FastFoodChain taco2 = establishmentRepository.save(new FastFoodChain("Taco Barn - Location 2", "900 Chain St", "Watauga", "Taco Barn"));
        addDemoRating(taco2, demoUser, 4, "Much better than the Hurst location.");

        FastFoodChain pizza1 = establishmentRepository.save(new FastFoodChain("Pizza Vortex - Location 1", "1000 Chain St", "Bedford", "Pizza Vortex"));
        addDemoRating(pizza1, demoUser, 3, "Middle of the road.");

        FastFoodChain pizza2 = establishmentRepository.save(new FastFoodChain("Pizza Vortex - Location 2", "1100 Chain St", "Euless", "Pizza Vortex"));
        addDemoRating(pizza2, demoUser, 5, "Best fountain drink in Euless.");

        FastFoodChain pizza3 = establishmentRepository.save(new FastFoodChain("Pizza Vortex - Location 3", "1200 Chain St", "North Richland Hills", "Pizza Vortex"));
        addDemoRating(pizza3, demoUser, 4, "Consistently crisp.");

        FastFoodChain sub1 = establishmentRepository.save(new FastFoodChain("Sub Galaxy - Location 1", "1300 Chain St", "Keller", "Sub Galaxy"));
        addDemoRating(sub1, demoUser, 2, "Disappointing, expected better.");

        FastFoodChain sub2 = establishmentRepository.save(new FastFoodChain("Sub Galaxy - Location 2", "1400 Chain St", "Saginaw", "Sub Galaxy"));
        addDemoRating(sub2, demoUser, 4, "Good, would recommend.");
    }

    private void addDemoRating(Establishment establishment, User user, int score, String comment) {
        establishment.addRating(new Rating(score, comment, user, establishment));
        establishmentRepository.save(establishment);
    }

}
