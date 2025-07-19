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

import com.devcamp.bery_real_estate.dtos.EmployeeDto;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.services.IEmployeeService;

@RestController
@CrossOrigin
@RequestMapping("/")
public class EmployeeController {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private IEmployeeService employeeService;

    /**
     * get all 
     * @param page
     * @param size
     * @return
     */
    @GetMapping("/employees")
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(@RequestParam(required = true) int page,
            @RequestParam(required = true) int size) {
        try {
            Page<EmployeeDto> employeeDtos = employeeService
                    .getAllEmployees(page, size)
                    .map(employee -> modelMapper.map(employee, EmployeeDto.class));
            return new ResponseEntity<>(employeeDtos, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('AGENT') or hasRole('ADMIN')")
    @GetMapping("/employees/count")
    public ResponseEntity<Object> countAllEmployees() {
        try {
            return ResponseEntity.ok(employeeService.countAllEmployees());
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
    @GetMapping("/employees/{employeeId}")
    public ResponseEntity<Object> getEmployeeById(
            @PathVariable(name = "employeeId", required = true) Integer id) {
        try {
            Employee employee = employeeService
                    .getEmployeeById(id);
            // convert sang dto
            EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
            return new ResponseEntity<>(employeeDto, HttpStatus.OK);
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
     * @param pEmployeeDto
     * @return
     */
    @PostMapping("/employees")
    public ResponseEntity<Object> createEmployee(@Valid @RequestBody EmployeeDto pEmployeeDto) {
        try {
            // convert sang entity
            Employee pEmployee = modelMapper.map(pEmployeeDto, Employee.class);
            // xử lý dữ liệu
            Employee employee = employeeService.createEmployee(pEmployee);
            // convert sang dto
            EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
            return new ResponseEntity<>(employeeDto, HttpStatus.CREATED);
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
     * update
     * @param id
     * @param pEmployeeDto
     * @return
     */
    @PutMapping("/employees/{employeeId}")
    public ResponseEntity<Object> updateEmployee(@PathVariable(name = "employeeId", required = true) Integer id,
            @Valid @RequestBody EmployeeDto pEmployeeDto) {
        try {
            // convert sang entity
            Employee pEmployee = modelMapper.map(pEmployeeDto, Employee.class);
            // xử lý dữ liệu
            Employee employee = employeeService.updateEmployee(id, pEmployee);
            // convert sang dto
            EmployeeDto employeeDto = modelMapper.map(employee, EmployeeDto.class);
            return new ResponseEntity<>(employeeDto, HttpStatus.OK);
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
     * @param id
     * @return
     */
    @DeleteMapping("/employees/{employeeId}")
    public ResponseEntity<Object> deleteEmployee(@PathVariable(name = "employeeId") Integer id) {
        try {
            employeeService.deleteEmployee(id);
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
