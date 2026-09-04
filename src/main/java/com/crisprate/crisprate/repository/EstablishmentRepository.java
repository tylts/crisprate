package com.crisprate.crisprate.repository;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.FastFoodChain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

    List<Establishment> findByNameContainingIgnoreCase(String name);

    List<Establishment> findByCityContainingIgnoreCase(String city);

    @Query("SELECT f FROM FastFoodChain f WHERE f.chainName = :chainName")
    List<FastFoodChain> findAllByChainName(@Param("chainName") String chainName);
}
