package com.devcamp.bery_real_estate.services;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.MasterLayout;

public interface IMasterLayoutService {
    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<MasterLayout> getAllMasterLayouts(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    MasterLayout getMasterLayoutById(Integer id);

    /**
     * add
     * @param projectId
     * @param pMasterLayout
     * @return
     */
    MasterLayout createMasterLayout(Integer projectId, MasterLayout pMasterLayout);

    /**
     * update
     * @param projectId
     * @param id
     * @param pMasterLayout
     * @return
     */
    MasterLayout updateMasterLayout(Integer projectId, Integer id, MasterLayout pMasterLayout);

    /**
     * delete
     * @param id
     */
    void deleteMasterLayout(Integer id);
}
