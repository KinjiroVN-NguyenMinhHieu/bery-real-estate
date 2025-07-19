package com.devcamp.bery_real_estate.services;

import java.util.List;
import java.util.Set;

import com.devcamp.bery_real_estate.entities.Project;
import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Street;
import com.devcamp.bery_real_estate.models.Ward;

public interface IDistrictService {

    /**
     * get all
     * @return
     */
    List<District> getAllDistricts();

    /**
     * get wards by id
     * @param id
     * @return
     */
    Set<Ward> getWardsByDistrictId(Integer id);

    /**
     * get streets by id
     * @param id
     * @return
     */
    Set<Street> getStreetsByDistrictId(Integer id);

    /**
     * get projects by id
     * @param id
     * @return
     */
    Set<Project> getProjectsByDistrictId(Integer id);

    /**
     * get by id
     * @param id
     * @return
     */
    District getDistrictById(Integer id);

    /**
     * add
     * @param pDistrict
     * @return
     */
    District createDistrict(District pDistrict);

    /**
     * update
     * @param id
     * @param pDistrict
     * @return
     */
    District updateDistrict(Integer id, District pDistrict);

    /**
     * delete
     * @param id
     */
    void deleteDistrict(Integer id);
}
