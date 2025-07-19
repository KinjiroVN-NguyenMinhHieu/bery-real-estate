package com.devcamp.bery_real_estate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.entities.Utility;

public interface IUtilityRepository extends JpaRepository<Utility, Integer> {

}
