package com.devcamp.bery_real_estate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.models.Location;

public interface ILocationRepository extends JpaRepository<Location, Integer> {

}
