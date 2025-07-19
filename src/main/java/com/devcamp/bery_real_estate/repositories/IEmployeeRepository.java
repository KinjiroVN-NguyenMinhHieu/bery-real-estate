package com.devcamp.bery_real_estate.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.entities.RealEstate;

public interface IEmployeeRepository extends JpaRepository<Employee, Integer> {
    /**
     * find by username
     * @param username
     * @return
    */
    Optional<Employee> findByUserName(String username);

    /**
     * check exist by username
     * @param username
     * @return
    */
    boolean existsByUserName(String username);

    /**
     * check exist by email
     * @param email
     * @return
    */
    boolean existsByEmail(String email);

    /**
     * find by email
     * @param email
     * @return
    */
    Optional<Employee> findByEmail(String email);

    /**
     * find favourite by id
     * @param employeeId
     * @param pageable
     * @return
    */
    @Query("SELECT re FROM Employee e JOIN e.favouriteRealEstates re WHERE e.id = :employeeId ORDER BY re.status DESC, re.createdAt DESC, re.updatedAt DESC")
    Page<RealEstate> findFavoriteRealEstatesByEmployeeId(@Param("employeeId") Integer employeeId, Pageable pageable);
}
