package com.devcamp.bery_real_estate.services;

import java.util.List;

import com.devcamp.bery_real_estate.models.Location;

public interface ILocationService {

    /**
     * get all
     * @return
     */
    List<Location> getAllLocations();

    /**
     * get by id
     * @param id
     * @return
     */
    Location getLocationById(Integer id);

    /**
     * add
     * @param pLocation
     * @return
     */
    Location createLocation(Location pLocation);

    /**
     * update
     * @param id
     * @param pLocation
     * @return
     */
    Location updateLocation(Integer id, Location pLocation);

    /**
     * delete
     * @param id
     */
    void deleteLocation(Integer id);
}
