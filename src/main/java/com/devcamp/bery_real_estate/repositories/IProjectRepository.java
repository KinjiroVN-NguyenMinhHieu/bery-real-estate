package com.devcamp.bery_real_estate.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devcamp.bery_real_estate.entities.Project;

public interface IProjectRepository extends JpaRepository<Project, Integer> {
    /**
     * find by district id
     * @param id
     * @return
    */
    @Query("SELECT p FROM Project p WHERE p.district.id = :districtId")
    Set<Project> findByDistrictId(@Param("districtId") int id);
}
