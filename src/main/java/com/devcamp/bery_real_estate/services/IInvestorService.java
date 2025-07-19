package com.devcamp.bery_real_estate.services;

import java.util.List;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.Investor;

public interface IInvestorService {
    /**
     * get list
     * @return
     */
    List<Investor> getListInvestors();

    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<Investor> getAllInvestors(int page, int size);

    /**
     * get by id
     * @param id
     * @return
     */
    Investor getInvestorById(Integer id);

    /**
     * add
     * @param addressId
     * @param pInvestor
     * @return
     */
    Investor createInvestor(Integer addressId, Investor pInvestor);

    /**
     * update
     * @param addressId
     * @param id
     * @param pInvestor
     * @return
     */
    Investor updateInvestor(Integer addressId, Integer id, Investor pInvestor);

    /**
     * delete
     * @param id
     */
    void deleteInvestor(Integer id);
}
