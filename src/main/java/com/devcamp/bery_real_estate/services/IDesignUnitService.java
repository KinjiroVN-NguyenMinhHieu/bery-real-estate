package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.DesignUnit;

public interface IDesignUnitService {
    /**
     * get list
     * @return
     */
    List<DesignUnit> getListDesignUnits();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<DesignUnit> getAllDesignUnits(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    DesignUnit getDesignUnitById(Integer id);

    /**
     * add
     * @param addressId
     * @param pDesignUnit
     * @return
     */
    DesignUnit createDesignUnit(Integer addressId, DesignUnit pDesignUnit);

    /**
     * update
     * @param addressId
     * @param id
     * @param pDesignUnit
     * @return
     */
    DesignUnit updateDesignUnit(Integer addressId, Integer id, DesignUnit pDesignUnit);

    /**
     * delete
     * @param id
     */
    void deleteDesignUnit(Integer id);
}
