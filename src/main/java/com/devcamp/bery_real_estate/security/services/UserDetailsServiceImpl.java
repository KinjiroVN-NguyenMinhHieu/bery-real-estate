package com.devcamp.bery_real_estate.security.services;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.devcamp.bery_real_estate.configs.StorageConfig;
import com.devcamp.bery_real_estate.constants.ERole;
import com.devcamp.bery_real_estate.dtos.PasswordDto;
import com.devcamp.bery_real_estate.dtos.ProfileDto;
import com.devcamp.bery_real_estate.dtos.VerifyUserDto;
import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.entities.RealEstate;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.models.Role;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.repositories.IRealEstateRepository;
import com.devcamp.bery_real_estate.security.jwt.JwtUtils;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  // Inject repository để thao tác với dữ liệu người dùng
  @Autowired
  private IEmployeeRepository employeeRepository;

  @Autowired
  private IRealEstateRepository realEstateRepository;
  
  @Autowired
  private EmailService emailService; // Inject EmailService để gửi email

  // Inject JwtUtils để xử lý JWT
  @Autowired
  private JwtUtils jwtUtils;

  // Inject BlackListingService để xử lý logout
  @Autowired
  @Lazy
  private BlackListingService blackListingService;
  
  // Inject PasswordEncoder để mã hóa mật khẩu
  @Autowired
  @Lazy
  private PasswordEncoder encoder;

  //Inject storage config
  @Autowired
  private StorageConfig storageConfig;

  // Lấy url app
  @Value("${app.base.url}")
  private String appURL;

  // Phương thức được ghi đè từ interface UserDetailsService để load thông tin
  // người dùng bằng username
  @Override
  @Transactional
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Tìm người dùng trong cơ sở dữ liệu bằng username
    Employee employee = employeeRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

    // Trả về UserDetailsImpl, một đối tượng UserDetails được tạo từ thông tin người
    // dùng
    return UserDetailsImpl.build(employee);
  }

  // Phương thức để xác thực và lấy thông tin người dùng cơ bản từ token JWT
  public VerifyUserDto verifyUser(HttpServletRequest req) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    // Lấy danh sách bất động sản của người dùng có trạng thái là PENDING hoặc APPROVED và chưa bị xóa
    List<RealEstate> realEstates = realEstateRepository.findAllPublishedlByEmployeeId(employee.getId());

    // Đếm số lượng bất động sản có trạng thái PENDING hoặc APPROVED
    int realEstatesCount = realEstates.size();

    // Trả về VerifyUserDto với username và số lượng bất động sản PENDING hoặc
    // APPROVED
    return new VerifyUserDto(employee.getUserName(), realEstatesCount);
  }

  // Phương thức để xác thực admin từ token JWT
  public boolean verifyAdmin(HttpServletRequest req) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    // Lấy danh sách role
    Set<Role> roles = employee.getRoles();

    return roles.stream()
                .anyMatch(role -> ERole.ROLE_ADMIN.name().equals(role.getName().toString()));
  }

  // Phương thức kiểm tra realestates đã thích
  public boolean isFavorite(HttpServletRequest req, int estatesId) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    // Lấy danh sách bất động sản mà người dùng đã thích
    Set<RealEstate> favouriteRealEstates = employee.getFavouriteRealEstates();

    return favouriteRealEstates.stream()
                       .anyMatch(realEstate -> realEstate.getId() == estatesId);
  }

  // Phương thức lấy danh sách realestates đã thích
  public Page<RealEstate> getFavorite(HttpServletRequest req, int size) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    Pageable pageable = PageRequest.of(0, size);

    return employeeRepository.findFavoriteRealEstatesByEmployeeId(employee.getId(), pageable);
  }

  // Phương thức thêm realestate yêu thích
  public void addFavourite(HttpServletRequest req, int estatesId) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    // check bđs
    RealEstate realEstate = realEstateRepository.findById(estatesId)
                    .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));

    // Thêm real estate vào danh sách yêu thích nếu chưa tồn tại
    if (!employee.getFavouriteRealEstates().contains(realEstate)) {
      employee.getFavouriteRealEstates().add(realEstate);
      employeeRepository.save(employee);
    } else {
        // Hoặc bạn có thể xử lý lỗi hoặc thông báo tại đây nếu cần
        throw new RuntimeException("Real Estate is already in favorites.");
    }
  }

  // Phương thức xóa realestate yêu thích
  public void removeFavourite(HttpServletRequest req, int estatesId) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);

    // check bđs
    RealEstate realEstate = realEstateRepository.findById(estatesId)
                    .orElseThrow(() -> new ResourceNotFoundException("Real Estate not found"));

    // Xóa real estate khỏi danh sách yêu thích nếu chưa tồn tại
    if (employee.getFavouriteRealEstates().contains(realEstate)) {
      employee.getFavouriteRealEstates().remove(realEstate);
      employeeRepository.save(employee);
    } else {
        // Hoặc bạn có thể xử lý lỗi hoặc thông báo tại đây nếu cần
        throw new RuntimeException("Real Estate is not exists in favorites.");
    }
  }

  // Phương thức get profile ng dùng
  public Employee getProfile(HttpServletRequest req) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);
    return employee;
  }

  // Phương thức change profile ng dùng
  public Employee changeProfile(HttpServletRequest req, ProfileDto pProfileDto, MultipartFile photoFile) throws IOException {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);
    employee.setLastName(pProfileDto.getLastName());
    employee.setFirstName(pProfileDto.getFirstName());
    employee.setUserName(pProfileDto.getUserName());
    employee.setEmail(pProfileDto.getEmail());
    employee.setBirthDate(pProfileDto.getBirthDate());
    employee.setAddress(pProfileDto.getAddress());
    employee.setCity(pProfileDto.getCity());
    employee.setCountry(pProfileDto.getCountry());
    employee.setHomePhone(pProfileDto.getHomePhone());
    employee.setNote(pProfileDto.getNote());
    //lưu ảnh và link ảnh
    if (pProfileDto.getPhotoFile() != null) {
      String photo = savePhoto(employee.getId(), photoFile);
      employee.setPhoto(photo);
    }

    return employeeRepository.save(employee);
  }

  // Phương thức thay đổi password ng dùng
  public void changePassword(HttpServletRequest req, PasswordDto passwordDto) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);
    //do mỗi lần mã hóa sẽ ra 1 chuỗi khác nhau nên cần dùng matches thay vì equals
    if (!encoder.matches(passwordDto.getOldPassword(), employee.getPassword())) {
      throw new IllegalArgumentException("Password incorrect!");
    } else if (encoder.matches(passwordDto.getNewPassword(), employee.getPassword())) {
      throw new IllegalArgumentException("The new password must be different from the old password!");
    }
    employee.setPassword(encoder.encode(passwordDto.getNewPassword()));
    employeeRepository.save(employee);
  }

  // Phương thức gửi email forgot password
  public void forgotPassword(HttpServletRequest req, String email) {
    String token = jwtUtils.generateForgotPasswordJwtToken(email); // Tạo token đặt lại mật khẩu
    String resetUrl = appURL + "/reset-password.html?token=" + token; // Tạo URL để đặt lại mật khẩu
    emailService.sendEmail(email, "Reset Password", "To reset your password, click the link below:\n" + resetUrl); // Gửi email chứa liên kết đặt lại mật khẩu// Trả về thông báo thành công
  }

  // Phương thức reset password
  public void resetPassword(HttpServletRequest req, PasswordDto passwordDto) {
    // Tìm ng dùng bằng req
    Employee employee = this.whoami(req);
    employee.setPassword(encoder.encode(passwordDto.getNewPassword()));
    employeeRepository.save(employee);
  }

  /**
   * Phương thức nội bộ để xử lý logout.
   *
   * @param req HttpServletRequest từ client
   * @return boolean chỉ ra kết quả xử lý logout
   */
  public boolean logoutInternal(HttpServletRequest req) {
    // Giải mã token từ request
    String token = resolveToken(req);
    if (token == null) {
      return false;
    }

    // Lấy username từ token JWT
    String username = jwtUtils.getUserNameFromJwtToken(token);

    // Đưa JWT vào danh sách đen và kiểm tra kết quả
    String blackListedJwt = blackListingService.blackListJwt(token, username);
    return blackListedJwt != null;
  }

  //Hàm lưu ảnh và trả về link ảnh
  private String savePhoto(Integer id, MultipartFile photoFile) throws IOException {
    // Xóa toàn bộ ảnh cũ trước khi lưu ảnh mới
    deleteOldPhotos(id);

    // Xây dựng đường dẫn lưu trữ dựa trên đường dẫn cấu hình và tên tệp tin
    StringBuilder builder = new StringBuilder();
    builder.append(storageConfig.getLocation() + "avatars/");
    builder.append(String.valueOf(id)).append("/");
    builder.append(photoFile.getOriginalFilename());
    Path path = Paths.get(builder.toString());

    // Lấy dữ liệu MultipartFile
    byte[] bytes = photoFile.getBytes();

    // Tạo thư mục cha nếu nó không tồn tại
    Files.createDirectories(path.getParent());

    // Ghi dữ liệu từ MultipartFile vào tệp tin
    Files.write(path, bytes);

    // Tạo đường dẫn tương đối để truy cập từ client và thêm vào danh sách link ảnh
    String photoUrl = appURL + "/images/avatars/" + id + "/" + photoFile.getOriginalFilename();

    return photoUrl;
  }

  //Hàm xóa bỏ file trong folder cũ đi để update ảnh
  private void deleteOldPhotos(Integer id) throws IOException {
    // Đường dẫn thư mục chứa các ảnh
    Path directoryPath = Paths.get(storageConfig.getLocation() + "avatars/").resolve(String.valueOf(id));

    // Kiểm tra xem thư mục tồn tại và có phải là một thư mục không
    if (Files.exists(directoryPath) && Files.isDirectory(directoryPath)) {
      // Xóa toàn bộ các tệp tin trong thư mục
      Files.walk(directoryPath)//duyệt qua tất cả các tệp và thư mục trong một cây thư mục
      .filter(Files::isRegularFile)//lọc ra chỉ các tệp thực sự, loại bỏ các thư mục và các loại tệp khác.
      .forEach(file -> {
              try {
              Files.delete(file);
              } catch (IOException e) {
              e.printStackTrace();
              }
      });
    }
  }

  // Phương thức để xác thực và lấy thông tin người dùng cơ bản từ token JWT
  private Employee whoami(HttpServletRequest req) {
    // Giải mã token từ request
    String token = resolveToken(req);

    // Lấy username từ token JWT
    String username = jwtUtils.getUserNameFromJwtToken(token);

    // Tìm người dùng trong cơ sở dữ liệu bằng username
    Employee employee = employeeRepository.findByUserName(username)
        .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

    return employee;
  }

  // Phương thức để giải mã token từ header Authorization trong request
  private String resolveToken(HttpServletRequest req) {
    String bearerToken = req.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }
}
