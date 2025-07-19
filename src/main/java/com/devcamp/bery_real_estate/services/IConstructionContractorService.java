package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.ConstructionContractor;

public interface IConstructionContractorService {
    /**
     * get list
     * @return
     */
    List<ConstructionContractor> getListConstructionContractors();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<ConstructionContractor> getAllConstructionContractors(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    ConstructionContractor getConstructionContractorById(Integer id);

    /**
     * add
     * @param addressId
     * @param pConstructionContractor
     * @return
     */
    ConstructionContractor createConstructionContractor(Integer addressId, ConstructionContractor pConstructionContractor);

    /**
     * update
     * @param addressId
     * @param id
     * @param pConstructionContractor
     * @return
     */
    ConstructionContractor updateConstructionContractor(Integer addressId, Integer id, ConstructionContractor pConstructionContractor);

    /**
     * delete
     * @param id
     */
    void deleteConstructionContractor(Integer id);
}
