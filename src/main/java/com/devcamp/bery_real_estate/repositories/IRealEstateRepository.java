package com.devcamp.bery_real_estate.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.devcamp.bery_real_estate.constants.EStatus;
import com.devcamp.bery_real_estate.entities.RealEstate;

public interface IRealEstateRepository
                extends JpaRepository<RealEstate, Integer>, JpaSpecificationExecutor<RealEstate> {
        /**
         * find all(page)
         * @param pageable
         * @return
        */
        @Query("SELECT r FROM RealEstate r WHERE r.isDeleted = false AND r.status = 'APPROVED' ORDER BY r.createdAt DESC, r.updatedAt DESC")
        Page<RealEstate> findAllWithPagination(Pageable pageable);

        /**
         * find all luxury(page)
         * @param pageable
         * @return
         */
        @Query("SELECT r FROM RealEstate r WHERE r.isDeleted = false AND r.status = 'APPROVED' AND r.price >= 1000 ORDER BY r.price DESC, r.createdAt DESC, r.updatedAt DESC")
        Page<RealEstate> findAllLuxuryWithPagination(Pageable pageable);

        /**
         * count by province
         * @return
         */
        @Query(value = "SELECT p._code AS province_code, COUNT(r.id) AS count FROM `realestate` r  JOIN `province` p ON r.province_id = p.id WHERE r.province_id IN (1, 2, 3, 4) AND r.is_deleted = false GROUP BY p._code", nativeQuery = true)
        List<Object[]> countByProvince();

        /**
         * find limit publish(page)
         * @param id
         * @param pageable
         * @return
         */
        @Query("SELECT r FROM RealEstate r JOIN r.employee e WHERE e.id = :id AND r.status IN ('APPROVED', 'PENDING') AND r.isDeleted = false")
        Page<RealEstate> findLimitPublishedByEmployeeId(@Param("id") Integer id, Pageable pageable);

        /**
         * find limit unpublish(page)
         * @param id
         * @param pageable
         * @return
         */
        @Query("SELECT r FROM RealEstate r JOIN r.employee e WHERE e.id = :id AND r.status IN ('REJECTED', 'REMOVED', 'COMPLETED') AND r.isDeleted = false")
        Page<RealEstate> findLimitUnpublishedByEmployeeId(@Param("id") Integer id, Pageable pageable);

        /**
         * find all publish by emp id
         * @param id
         * @return
         */
        @Query("SELECT r FROM RealEstate r JOIN r.employee e WHERE e.id = :id AND r.status IN ('APPROVED', 'PENDING') AND r.isDeleted = false ORDER BY r.status DESC, r.createdAt DESC, r.updatedAt DESC")
        List<RealEstate> findAllPublishedlByEmployeeId(@Param("id") Integer id);

        /**
         * find all unpublish by emp id
         * @param id
         * @return
         */
        @Query("SELECT r FROM RealEstate r JOIN r.employee e WHERE e.id = :id AND r.status IN ('REJECTED', 'REMOVED', 'COMPLETED') AND r.isDeleted = false ORDER BY r.status DESC, r.createdAt DESC, r.updatedAt DESC")
        List<RealEstate> findAllUnpublishedByEmployeeId(@Param("id") Integer id);

        /**
         * find all by status(page)
         * @param eStatus
         * @param pageable
         * @return
         */
        @Query("SELECT r FROM RealEstate r WHERE r.status = 'PENDING'")
        Page<RealEstate> findAllByStatus(EStatus eStatus, Pageable pageable);

        /**
         * count by status
         * @param eStatus
         * @return
         */
        long countByStatus(EStatus eStatus);
}
