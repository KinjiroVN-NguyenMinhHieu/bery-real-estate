package com.devcamp.bery_real_estate.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devcamp.bery_real_estate.models.Photo;

public interface IPhotoRepository extends JpaRepository<Photo, Integer> {
    // Định nghĩa phương thức tìm kiếm theo RealEstate ID
    List<Photo> findByRealEstateId(Integer realEstateId);
}
