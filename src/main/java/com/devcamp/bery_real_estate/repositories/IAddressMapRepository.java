package com.devcamp.bery_real_estate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.entities.AddressMap;

public interface IAddressMapRepository extends JpaRepository<AddressMap, Integer> {

}
