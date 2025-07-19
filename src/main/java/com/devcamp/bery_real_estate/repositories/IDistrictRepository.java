package com.devcamp.bery_real_estate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.models.District;

public interface IDistrictRepository extends JpaRepository<District, Integer> {
    /**
     * fin all
     * @return
    */
    List<District> findAllByOrderByProvinceAscPrefixAscIdAscNameAsc();
}
