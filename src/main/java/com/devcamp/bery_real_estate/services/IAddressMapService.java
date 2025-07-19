package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.AddressMap;

public interface IAddressMapService {
    /**
     * get list
     * @return
     */
    List<AddressMap> getListAddressMaps();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<AddressMap> getAllAddressMaps(int page, int size);
    
    /**
     * get by id
     * @param id
     * @return
     */
    AddressMap getAddressMapById(Integer id);

    /**
     * add
     * @param pAddressMap
     * @return
     */
    AddressMap createAddressMap(AddressMap pAddressMap);

    /**
     * update
     * @param id
     * @param pAddressMap
     * @return
     */
    AddressMap updateAddressMap(Integer id, AddressMap pAddressMap);

    /**
     * delete
     * @param id
     */
    void deleteAddressMap(Integer id);
}
