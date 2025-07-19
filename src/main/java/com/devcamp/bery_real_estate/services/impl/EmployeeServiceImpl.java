package com.devcamp.bery_real_estate.services.impl;

import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devcamp.bery_real_estate.constants.EActivated;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Role;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.repositories.IRoleRepository;
import com.devcamp.bery_real_estate.security.services.EmailService;
import com.devcamp.bery_real_estate.services.IEmployeeService;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    @Autowired
    private IEmployeeRepository employeeRepository;
    @Autowired
    private IRoleRepository roleRepository;
    // Inject PasswordEncoder để mã hóa mật khẩu
    @Autowired
    private PasswordEncoder encoder;
    // Inject EmailService để gửi email
    @Autowired
    private EmailService emailService; 
    private final String defaultPassword = "123456";

    @Override
    public Page<Employee> getAllEmployees(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return employeeRepository.findAll(pageable);
    };

    @Override
    public long countAllEmployees() {
        return employeeRepository.count();
    }

    @Override
    public Employee getEmployeeById(Integer id) {
        return employeeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    };

    @Override
    @Transactional
    public Employee createEmployee(Employee pEmployee) {
        // Kiểm tra danh sách các role
        HashSet<Role> roles = new HashSet<>();
        for (Role role : pEmployee.getRoles()) {
            role = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            roles.add(role);
        }

        //Tạo mới 1 employee rỗng
        Employee employee = new Employee();
        employee.setLastName(pEmployee.getLastName());
        employee.setFirstName(pEmployee.getFirstName());
        employee.setUserName(pEmployee.getUserName());
        employee.setEmail(pEmployee.getEmail());
        // Kiểm tra nếu mật khẩu từ pEmployee là null hoặc rỗng
        if (pEmployee.getPassword() == null || pEmployee.getPassword().trim().isEmpty()) {
            // Mật khẩu không hợp lệ, sử dụng mật khẩu mặc định
            String encodedDefaultPassword = encoder.encode(defaultPassword);
            employee.setPassword(encodedDefaultPassword);

            // Gửi email với mật khẩu mới
            emailService.sendEmail(pEmployee.getEmail(), "New Password", "Your new password: " + defaultPassword); 
        } else {
            // Mật khẩu hợp lệ, mã hóa mật khẩu từ pEmployee
            String encodedPassword = encoder.encode(pEmployee.getPassword());
            employee.setPassword(encodedPassword);
        }
        employee.setBirthDate(pEmployee.getBirthDate());
        employee.setAddress(pEmployee.getAddress());
        employee.setCity(pEmployee.getCity());
        employee.setCountry(pEmployee.getCountry());
        employee.setHomePhone(pEmployee.getHomePhone());
        employee.setPhoto(pEmployee.getPhoto());
        employee.setNote(pEmployee.getNote());
        employee.setRoles(roles);
        employee.setActivated(pEmployee.getActivated());
        employee.setUpdatedAt(new Date());

        return employeeRepository.save(employee);
    }

    @Override
    @Transactional
    public Employee updateEmployee(Integer id, Employee pEmployee) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        // Kiểm tra danh sách các role
        HashSet<Role> roles = new HashSet<>();
        for (Role role : pEmployee.getRoles()) {
            role = roleRepository.findByName(role.getName())
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            roles.add(role);
        }

        employee.setLastName(pEmployee.getLastName());
        employee.setFirstName(pEmployee.getFirstName());
        employee.setUserName(pEmployee.getUserName());
        employee.setEmail(pEmployee.getEmail());
        employee.setBirthDate(pEmployee.getBirthDate());
        employee.setAddress(pEmployee.getAddress());
        employee.setCity(pEmployee.getCity());
        employee.setCountry(pEmployee.getCountry());
        employee.setHomePhone(pEmployee.getHomePhone());
        employee.setPhoto(pEmployee.getPhoto());
        employee.setNote(pEmployee.getNote());
        employee.setRoles(roles);
        employee.setActivated(pEmployee.getActivated());
        employee.setUpdatedAt(new Date());

        return employeeRepository.save(employee);
    };

    @Override
    public void deleteEmployee(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setActivated(EActivated.N);
        employee.setUpdatedAt(new Date());
        employeeRepository.save(employee);
    };
}
