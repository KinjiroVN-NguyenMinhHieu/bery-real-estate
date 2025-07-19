package com.devcamp.bery_real_estate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.entities.ConstructionContractor;

public interface IConstructionContractorRepository extends JpaRepository<ConstructionContractor, Integer> {

}
