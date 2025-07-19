package com.devcamp.bery_real_estate;

import java.util.HashSet;
import java.util.Set;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.devcamp.bery_real_estate.constants.ERole;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.models.Role;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.repositories.IRoleRepository;

@SpringBootApplication
public class BeryRealEstateApplication implements CommandLineRunner {

	// Inject các repository và encoder cần thiết
	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private IEmployeeRepository employeeRepository;

	@Autowired
	private PasswordEncoder encoder;

	@Bean
	public ModelMapper ModelMapper() {
		return new ModelMapper();
	}

	public static void main(String[] args) {
		SpringApplication.run(BeryRealEstateApplication.class, args);
	}

	// Phương thức này được thực thi khi ứng dụng chạy
	@Override
	public void run(String... params) throws Exception {
		// Tạo các role nếu chúng không tồn tại trong cơ sở dữ liệu
		if (!roleRepository.findByName(ERole.ROLE_USER).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_USER));
		}

		if (!roleRepository.findByName(ERole.ROLE_AGENT).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_AGENT));
		}

		if (!roleRepository.findByName(ERole.ROLE_ADMIN).isPresent()) {
			roleRepository.save(new Role(ERole.ROLE_ADMIN));
		}

		// Tạo một user mẫu nếu nó không tồn tại trong cơ sở dữ liệu
		if (!employeeRepository.existsByUserName("DevcampUser")) {
			Employee initEmployee = new Employee("DevcampUser", "demo@devcamp.edu.vn", encoder.encode("123456"));
			Set<Role> roles = new HashSet<>();
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			Role agentRole = roleRepository.findByName(ERole.ROLE_AGENT)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
			roles.add(agentRole);
			roles.add(adminRole);
			initEmployee.setRoles(roles);

			if (!employeeRepository.existsByEmail("demo@devcamp.edu.vn")) {
				employeeRepository.save(initEmployee);
			}
		}
	}

}
