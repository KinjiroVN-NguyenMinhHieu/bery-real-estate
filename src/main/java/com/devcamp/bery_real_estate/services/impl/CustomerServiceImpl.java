package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devcamp.bery_real_estate.entities.Customer;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.ICustomerRepository;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.services.ICustomerService;

@Service
public class CustomerServiceImpl implements ICustomerService {
    @Autowired
    private ICustomerRepository customerRepository;
    @Autowired
    private IEmployeeRepository employeeRepository;

    @Override
    public Page<Customer> getAllCustomers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return customerRepository.findAll(pageable);
    };

    @Override
    public long countAllCustormers() {
        return customerRepository.count();
    }

    @Override
    public Customer getCustomerById(Integer id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
    };

    @Override
    @Transactional
    public Customer createCustomer(Customer pCustomer) {
        Customer customer = new Customer();
        customer.setContactName(pCustomer.getContactName());
        customer.setContactTitle(pCustomer.getContactTitle());
        customer.setAddress(pCustomer.getAddress());
        customer.setMobile(pCustomer.getMobile());
        customer.setEmail(pCustomer.getEmail());
        customer.setNote(pCustomer.getNote());
        // Lấy thông tin người dùng hiện tại(cho phép bất kì user)
        Employee currentEmployee = this.getCurrentEmployee();
        customer.setCreatedBy(currentEmployee);
        return customerRepository.save(customer);
    };

    @Override
    @Transactional
    public Customer updateCustomer(Integer id, Customer pCustomer) {
        // Lấy thông tin người dùng hiện tại
        Employee currentEmployee = this.getCurrentEmployee();
        //  ko cho phép cập nhật lung tung, cần có tài khoản
        if (currentEmployee == null) {
            throw new UsernameNotFoundException("User not found");
        }

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customer.setContactName(pCustomer.getContactName());
        customer.setContactTitle(pCustomer.getContactTitle());
        customer.setAddress(pCustomer.getAddress());
        customer.setMobile(pCustomer.getMobile());
        customer.setEmail(pCustomer.getEmail());
        customer.setNote(pCustomer.getNote());
        
        customer.setUpdatedBy(currentEmployee);
        customer.setUpdatedAt(new Date());
        return customerRepository.save(customer);
    };

    @Override
    public void deleteCustomer(Integer id) {
        customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        customerRepository.deleteById(id);
    };

    /**
     * Phương thức để lấy thông tin của nhân viên hiện tại dựa trên thông tin xác
     * thực của người dùng.
     * 
     * @return Đối tượng Employee đang được xác thực.
     * @throws UsernameNotFoundException Nếu không tìm thấy nhân viên với username
     *                                   được xác thực.
     */
    @Override
    public Employee getCurrentEmployee() {
        // Lấy đối tượng Authentication từ SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Lấy username từ đối tượng Authentication
        String username = authentication.getName();

        // Tìm kiếm nhân viên trong cơ sở dữ liệu bằng username
        return employeeRepository.findByUserName(username)
                // Ko tìm thấy thì return null
                .orElse(null);
    }

}
