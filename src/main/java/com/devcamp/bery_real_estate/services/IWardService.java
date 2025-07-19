package com.devcamp.bery_real_estate.services;

import java.util.List;

import com.devcamp.bery_real_estate.models.Ward;

public interface IWardService {

    /**
     * get all
     * @return
    */
    List<Ward> getAllWards();

    /**
     * get by id
     * @param id
     * @return
    */
    Ward getWardById(Integer id);

    /**
     * add
     * @param pWard
     * @return
    */
    Ward createWard(Ward pWard);

    /**
     * update
     * @param id
     * @param pWard
     * @return
    */
    Ward updateWard(Integer id, Ward pWard);

    /**
     * delete
     * @param id
    */
    void deleteWard(Integer id);
}
