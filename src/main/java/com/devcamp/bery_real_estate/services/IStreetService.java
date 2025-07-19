package com.devcamp.bery_real_estate.services;

import java.util.List;

import com.devcamp.bery_real_estate.models.Street;

public interface IStreetService {
    /**
     * get all
     * @return
     */
    List<Street> getAllStreets();

    /**
     * get by id
     * @param id
     * @return
     */
    Street getStreetById(Integer id);

    /**
     * add
     * @param pStreet
     * @return
     */
    Street createStreet(Street pStreet);

    /**
     * update
     * @param id
     * @param pStreet
     * @return
     */
    Street updateStreet(Integer id, Street pStreet);

    /**
     * delete
     * @param id
     */
    void deleteStreet(Integer id);
}
