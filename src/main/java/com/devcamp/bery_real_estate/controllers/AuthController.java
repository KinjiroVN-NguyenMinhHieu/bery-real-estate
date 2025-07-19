package com.devcamp.bery_real_estate.controllers;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.constants.ERole;
import com.devcamp.bery_real_estate.dtos.PasswordDto;
import com.devcamp.bery_real_estate.dtos.ProfileDto;
import com.devcamp.bery_real_estate.dtos.RealEstateDto;
import com.devcamp.bery_real_estate.dtos.VerifyUserDto;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Role;
import com.devcamp.bery_real_estate.payload.request.LoginRequest;
import com.devcamp.bery_real_estate.payload.request.RegisterRequest;
import com.devcamp.bery_real_estate.payload.response.JwtResponse;
import com.devcamp.bery_real_estate.payload.response.MessageResponse;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.repositories.IRoleRepository;
import com.devcamp.bery_real_estate.security.jwt.JwtUtils;
import com.devcamp.bery_real_estate.security.services.UserDetailsImpl;
import com.devcamp.bery_real_estate.security.services.UserDetailsServiceImpl;

import io.jsonwebtoken.JwtException;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

  // Inject AuthenticationManager để xác thực người dùng
  @Autowired
  private AuthenticationManager authenticationManager;

  // Inject repository để thao tác với dữ liệu người dùng và role
  @Autowired
  private IEmployeeRepository employeeRepository;

  @Autowired
  private IRoleRepository roleRepository;

  // Inject PasswordEncoder để mã hóa mật khẩu
  @Autowired
  private PasswordEncoder encoder;

  // Inject JwtUtils để xử lý JWT
  @Autowired
  private JwtUtils jwtUtils;

  @Autowired
  private ModelMapper modelMapper;

  // Inject UserDetailsServiceImpl để xử lý các end point liên quan đến user
  @Autowired
  private UserDetailsServiceImpl userDetailsServiceImpl;

  // Phương thức xác thực người dùng khi đăng nhập
  @PostMapping("/login")
  public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
    try {
      // Xác thực người dùng
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

      // Set thông tin xác thực vào SecurityContext
      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Tạo token JWT
      String jwt = jwtUtils.generateJwtToken(authentication);

      // Lấy thông tin người dùng và vai trò từ UserDetailsImpl
      UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      List<String> roles = userDetails.getAuthorities().stream()
          .map(item -> item.getAuthority())
          .collect(Collectors.toList());

      // Trả về token JWT và thông tin người dùng
      return ResponseEntity.ok(new JwtResponse(jwt,
          userDetails.getUsername(),
          // userDetails.getId(),
          // userDetails.getEmail(),
          roles));
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức đăng ký người dùng mới
  @PostMapping("/register")
  public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
    try {
      // Kiểm tra xem username đã tồn tại chưa
      if (employeeRepository.existsByUserName(registerRequest.getUsername())) {
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: Username is already taken!"));
      }

      // Kiểm tra xem email đã tồn tại chưa
      if (employeeRepository.existsByEmail(registerRequest.getEmail())) {
        return ResponseEntity
            .badRequest()
            .body(new MessageResponse("Error: Email is already in use!"));
      }

      // Tạo mới tài khoản người dùng
      Employee employee = new Employee(registerRequest.getUsername(),
          registerRequest.getEmail(),
          encoder.encode(registerRequest.getPassword()));

      // Xác định vai trò cho người dùng mới
      Set<String> strRoles = registerRequest.getRole();
      Set<Role> roles = new HashSet<>();

      if (strRoles == null) {
        Role userRole = roleRepository.findByName(ERole.ROLE_USER)
            .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
        roles.add(userRole);
      } else {
        strRoles.forEach(role -> {
          switch (role) {
            case "admin":
              Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                  .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
              roles.add(adminRole);
              break;
            case "agent":
              Role agentRole = roleRepository.findByName(ERole.ROLE_AGENT)
                  .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
              roles.add(agentRole);
              break;
            default:
              Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                  .orElseThrow(() -> new ResourceNotFoundException("Error: Role is not found."));
              roles.add(userRole);
          }
        });
      }

      // Gán vai trò cho người dùng và lưu vào cơ sở dữ liệu
      employee.setRoles(roles);
      employeeRepository.save(employee);

      // Trả về thông báo đăng ký thành công
      return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    } catch (ResourceNotFoundException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức nhận email forgot password
  @PostMapping("/forgot-password")
  public ResponseEntity<Object> forgotPassword(HttpServletRequest req, @RequestParam String email) {
    try {
      userDetailsServiceImpl.forgotPassword(req, email);
      return ResponseEntity.ok().build();
    } catch (ResourceNotFoundException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức reset password
  @PostMapping("/reset-password")
  public ResponseEntity<Object> resetPassword(HttpServletRequest req, @RequestBody PasswordDto pPasswordDto) {
    try {
      userDetailsServiceImpl.resetPassword(req, pPasswordDto);
      return ResponseEntity.ok().build();
    } catch (ResourceNotFoundException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (JwtException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức verify và lấy thông tin cơ bản ng dùng
  @GetMapping("/verify")
  public ResponseEntity<Object> userAccess(HttpServletRequest req) {
    try {
      VerifyUserDto verifyUserDto = userDetailsServiceImpl.verifyUser(req);
      return new ResponseEntity<>(verifyUserDto, HttpStatus.OK);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức verify admin
  @GetMapping("/verify-admin")
  public ResponseEntity<Object> verifyAdmin(HttpServletRequest req) {
    try {
      boolean isAdmin = userDetailsServiceImpl.verifyAdmin(req);
      return ResponseEntity.ok(isAdmin);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức kiểm tra bds yêu thích
  @GetMapping("/isfavourite/{estatesId}")
  public ResponseEntity<Object> isFavorite(HttpServletRequest req, @PathVariable(required = true) int estatesId) {
      try {
        boolean isFavorite = userDetailsServiceImpl.isFavorite(req, estatesId);
        return ResponseEntity.ok(isFavorite);
      } catch (UsernameNotFoundException e) {
        System.err.println(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  // Phương thức lấy danh sách bds yêu thích
  @GetMapping("/favourite/limit")
  public ResponseEntity<Object> getFavorite(HttpServletRequest req, @RequestParam(defaultValue = "10") int size) {
      try {
        Page<RealEstateDto> favouriteRealEstates = userDetailsServiceImpl
              .getFavorite(req, size)
              .map(realEstate -> modelMapper.map(realEstate, RealEstateDto.class));
        return new ResponseEntity<>(favouriteRealEstates, HttpStatus.OK);
      } catch (UsernameNotFoundException e) {
        System.err.println(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
      } catch (Exception e) {
        System.out.println(e.getMessage());
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  // Phương thức thêm bds yêu thích
  @PostMapping("/favourite/{estatesId}")
  public ResponseEntity<Object> addFavorite(HttpServletRequest req, @PathVariable int estatesId) {
    try {
      userDetailsServiceImpl.addFavourite(req, estatesId);
      return ResponseEntity.ok().build();
    } catch (ResourceNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức xóa bds yêu thích
  @PutMapping("/favourite/{estatesId}")
  public ResponseEntity<Object> removeFavourite(HttpServletRequest req, @PathVariable int estatesId) {
    try {
      userDetailsServiceImpl.removeFavourite(req, estatesId);
      return ResponseEntity.noContent().build();
    } catch (ResourceNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (RuntimeException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức thay đổi password ng dùng
  @PutMapping("/password")
  public ResponseEntity<Object> changePassword(HttpServletRequest req, @RequestBody PasswordDto pPasswordDto) {
    try {
      userDetailsServiceImpl.changePassword(req, pPasswordDto);
      return ResponseEntity.ok().build();
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IllegalArgumentException e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức get profile người dùng
  @GetMapping("/profile")
  public ResponseEntity<Object> getProfile(HttpServletRequest req) {
    try {
      Employee employee = userDetailsServiceImpl.getProfile(req);
      // convert qua dto
      ProfileDto profileDto = modelMapper.map(employee, ProfileDto.class);
      return new ResponseEntity<>(profileDto, HttpStatus.OK);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Phương thức thay đổi profile ng dùng
  @PutMapping("/profile")
  public ResponseEntity<Object> updateProfile(
      HttpServletRequest req,
      @Valid @ModelAttribute ProfileDto pProfileDto,
      @RequestParam(value = "photoFile", required = false) MultipartFile photoFile) {
    try {
      Employee employee = userDetailsServiceImpl.changeProfile(req, pProfileDto, photoFile);
      // convert qua dto
      ProfileDto profileDto = modelMapper.map(employee, ProfileDto.class);
      return new ResponseEntity<>(profileDto, HttpStatus.OK);
    } catch (UsernameNotFoundException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    } catch (IOException e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
    } catch (Exception e) {
      System.err.println(e.getMessage());
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  /**
   * Phương thức để đưa JWT từ yêu cầu của người dùng vào danh sách đen (logout).
   * Sử dụng @PutMapping để xác định phương thức PUT và path "/logout".
   * 
   * @param request HttpServletRequest - Đối tượng request của người dùng
   * @return ResponseEntity<Void> - Phản hồi HTTP 200 OK với nội dung rỗng
   */
  @PutMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletRequest request) {
    try {
      // Gọi phương thức nội bộ để xử lý logout
      Boolean isLogout = userDetailsServiceImpl.logoutInternal(request);
      if (!isLogout) {
        // JWT đã có trong danh sách đen, không cho logout
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
      }
      // Trả về phản hồi HTTP 200 OK với nội dung rỗng
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      // Xử lý ngoại lệ và trả về phản hồi HTTP 500 INTERNAL SERVER ERROR
      System.out.println("Error during logout: " + e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}
