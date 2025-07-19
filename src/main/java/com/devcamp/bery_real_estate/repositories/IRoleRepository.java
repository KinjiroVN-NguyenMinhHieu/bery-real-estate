package com.devcamp.bery_real_estate.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.constants.ERole;
import com.devcamp.bery_real_estate.models.Role;

public interface IRoleRepository extends JpaRepository<Role, Integer> {
  /**
   * find by name
   * @param name
   * @return
   */
  Optional<Role> findByName(ERole name);
}
