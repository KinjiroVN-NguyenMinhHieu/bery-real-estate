package com.devcamp.bery_real_estate.services;

import java.util.List;
import java.util.Set;

import com.devcamp.bery_real_estate.models.District;
import com.devcamp.bery_real_estate.models.Province;

public interface IProvinceService {

    /**
     * get all
     * @return
     */
    List<Province> getAllProvinces();

    /**
     * get districts by province id
     * @param id
     * @return
     */
    Set<District> getDistrictsByProvinceId(Integer id);

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    List<Province> getProvincesPanigation(String page,String size);

    /**
     * get by id
     * @param id
     * @return
     */
    Province getProvinceById(Integer id);

    /**
     * add
     * @param pProvince
     * @return
     */
    Province createProvince(Province pProvince);

    /**
     * update
     * @param id
     * @param pProvince
     * @return
     */
    Province updateProvine(Integer id, Province pProvince);

    /**
     * delete
     * @param id
     */
    void deleteProvince(Integer id);
}
