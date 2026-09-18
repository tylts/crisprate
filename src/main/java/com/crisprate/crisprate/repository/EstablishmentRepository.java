package com.crisprate.crisprate.repository;

import com.crisprate.crisprate.entity.Establishment;
import com.crisprate.crisprate.entity.FastFoodChain;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EstablishmentRepository extends JpaRepository<Establishment, Long> {

    Page<Establishment> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Establishment> findByCityContainingIgnoreCase(String city, Pageable pageable);

    @Query("SELECT f FROM FastFoodChain f WHERE f.chainName = :chainName")
    List<FastFoodChain> findAllByChainName(@Param("chainName") String chainName);
}
