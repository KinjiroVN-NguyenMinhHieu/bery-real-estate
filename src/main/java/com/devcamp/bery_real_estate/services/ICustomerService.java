package com.devcamp.bery_real_estate.services;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.Customer;
import com.devcamp.bery_real_estate.entities.Employee;

public interface ICustomerService {
    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<Customer> getAllCustomers(int page, int size);

    /**
     * count
     * @return
     */
    long countAllCustormers();

    /**
     * get by id
     * @param id
     * @return
     */
    Customer getCustomerById(Integer id);

    /**
     * add
     * @param pCustomer
     * @return
     */
    Customer createCustomer(Customer pCustomer);

    /**
     * update
     * @param id
     * @param pCustomer
     * @return
     */
    Customer updateCustomer(Integer id, Customer pCustomer);

    /**
     * delete
     * @param id
     */
    void deleteCustomer(Integer id);

    /**
     * get current emp
     * @return
     */
    Employee getCurrentEmployee();
}
