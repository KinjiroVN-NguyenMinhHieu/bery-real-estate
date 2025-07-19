package com.devcamp.bery_real_estate.controllers;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.devcamp.bery_real_estate.dtos.CustomerDto;
import com.devcamp.bery_real_estate.entities.Customer;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.ICustomerService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private ICustomerService customerService;

    /**
     * get all
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/customers")
    public ResponseEntity<Page<CustomerDto>> getAllCustomers(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            Page<CustomerDto> customerDtos = customerService
                    .getAllCustomers(page, size)
                    .map(customer -> modelMapper.map(customer, CustomerDto.class));
            return new ResponseEntity<>(customerDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * count
     * @return
     */
    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/customers/count")
    public ResponseEntity<Object> countAllCustormers() {
        try {
            return ResponseEntity.ok(customerService.countAllCustormers());
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * get by id
     * @param id
     * @return
     */
    @GetMapping("/customers/{customerId}")
    public ResponseEntity<Object> getCustomerById(
            @PathVariable(name = "customerId", required = true) Integer id) {
        try {
            Customer customer = customerService
                    .getCustomerById(id);
            // convert sang dto
            CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
            return new ResponseEntity<>(customerDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * add
     * @param pCustomerDto
     * @return
     */
    @PostMapping("/customers")
    public ResponseEntity<Object> createCustomer(@Valid @RequestBody CustomerDto pCustomerDto) {
        try {
            // convert sang entity
            Customer pCustomer = modelMapper.map(pCustomerDto, Customer.class);
            // xử lý dữ liệu
            Customer customer = customerService.createCustomer(pCustomer);
            // convert sang dto
            CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
            return new ResponseEntity<>(customerDto, HttpStatus.CREATED);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update
     * @param id
     * @param pCustomerDto
     * @return
     */
    @PutMapping("/customers/{customerId}")
    public ResponseEntity<Object> updateCustomer(@PathVariable(name = "customerId", required = true) Integer id,
            @Valid @RequestBody CustomerDto pCustomerDto) {
        try {
            // convert sang entity
            Customer pCustomer = modelMapper.map(pCustomerDto, Customer.class);
            // xử lý dữ liệu
            Customer customer = customerService.updateCustomer(id, pCustomer);
            // convert sang dto
            CustomerDto customerDto = modelMapper.map(customer, CustomerDto.class);
            return new ResponseEntity<>(customerDto, HttpStatus.OK);
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * delete
     */
    @DeleteMapping("/customers/{customerId}")
    public ResponseEntity<Object> deleteCustomer(@PathVariable(name = "customerId") Integer id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.noContent().build();
        } catch (ResourceNotFoundException e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
