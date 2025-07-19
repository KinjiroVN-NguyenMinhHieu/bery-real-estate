package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.Utility;

public interface IUtilityService {
    /**
     * get list
     * @return
    */
    List<Utility> getListUtilities();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
    */
    Page<Utility> getAllUtilities(int page, int size);

    /**
     * get by id
     * @param id
     * @return
    */
    Utility getUtilityById(Integer id);

    /**
     * add
     * @param pUtility
     * @return
    */
    Utility createUtility(Utility pUtility);

    /**
     * update
     * @param id
     * @param pUtility
     * @return
    */
    Utility updateUtility(Integer id, Utility pUtility);

    /**
     * delete
     * @param id
    */
    void deleteUtility(Integer id);
}
