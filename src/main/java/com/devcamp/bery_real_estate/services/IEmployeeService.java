package com.devcamp.bery_real_estate.services;

import org.springframework.data.domain.Page;

import com.devcamp.bery_real_estate.entities.Employee;

public interface IEmployeeService {
    /**
     * get all(page)
     * @param page
     * @param size
     * @return
     */
    Page<Employee> getAllEmployees(int page, int size);

    /**
     * count
     * @return
     */
    long countAllEmployees();

    /**
     * get by id
     * @param id
     * @return
     */
    Employee getEmployeeById(Integer id);

    /**
     * add
     * @param pEmployee
     * @return
     */
    Employee createEmployee(Employee pEmployee);

    /**
     * update
     * @param id
     * @param pEmployee
     * @return
     */
    Employee updateEmployee(Integer id, Employee pEmployee);

    /**
     * delete
     * @param id
     */
    void deleteEmployee(Integer id);
}
