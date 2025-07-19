package com.devcamp.bery_real_estate.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.entities.RegionLink;

public interface IRegionLinkRepository extends JpaRepository<RegionLink, Integer> {

}
