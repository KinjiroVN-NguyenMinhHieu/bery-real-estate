package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.RegionLink;

public interface IRegionLinkService {
    /**
     * get list
     * @return
     */
    List<RegionLink> getListRegionLinks();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<RegionLink> getAllRegionLinks(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    RegionLink getRegionLinkById(Integer id);

    /**
     * add
     * @param pRegionLink
     * @return
     */
    RegionLink createRegionLink(RegionLink pRegionLink);

    /**
     * update
     * @param id
     * @param pRegionLink
     * @return
     */
    RegionLink updateRegionLink(Integer id, RegionLink pRegionLink);

    /**
     * delete
     * @param id
     */
    void deleteRegionLink(Integer id);
}
