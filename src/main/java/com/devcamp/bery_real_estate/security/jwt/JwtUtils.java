package com.devcamp.bery_real_estate.security.jwt;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.devcamp.bery_real_estate.entities.Employee;
import com.devcamp.bery_real_estate.errors.ResourceNotFoundException;
import com.devcamp.bery_real_estate.repositories.IEmployeeRepository;
import com.devcamp.bery_real_estate.security.services.UserDetailsImpl;
import io.jsonwebtoken.*;

@Component
public class JwtUtils {
  private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

  // Lấy giá trị bí mật của JWT từ file cấu hình
  @Value("${devcamp.app.jwtSecret}")
  private String jwtSecret;

  // Lấy giá trị thời gian hết hạn của JWT từ file cấu hình
  @Value("${devcamp.app.jwtExpirationMs}")
  private int jwtExpirationMs;

  // Lấy giá trị thời gian hết hạn của forgotPasswordJWT từ file cấu hình
  @Value("${devcamp.app.forgotPasswordTokenExpirationMs}")
  private int jwtforgotPasswordExpirationMs;

  // Inject repository để thao tác với dữ liệu người dùng
  @Autowired
  private IEmployeeRepository employeeRepository;

  // Phương thức này tạo ra JWT token dựa trên thông tin xác thực của người dùng
  public String generateJwtToken(Authentication authentication) {
    // Lấy thông tin chi tiết của người dùng từ đối tượng Authentication
    UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

    // Tính toán thời gian hết hạn của token
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

    // Tạo JWT token với các thông tin cần thiết
    return Jwts.builder()
        .setSubject(userPrincipal.getUsername()) // Thiết lập subject của token là tên đăng nhập của người dùng
        .setIssuedAt(now) // Thiết lập thời gian phát hành token là thời điểm hiện tại
        .setExpiration(expiryDate) // Thiết lập thời gian hết hạn của token
        .signWith(SignatureAlgorithm.HS512, jwtSecret) // Ký token bằng thuật toán HS512 và bí mật jwtSecret
        .compact(); // Hoàn thiện token
  }

  // Phương thức này tạo ra JWT token dựa trên thông tin xác thực của người dùng
  public String generateForgotPasswordJwtToken(String email) {
    // Lấy thông tin chi tiết của người dùng từ email
    Employee employee = employeeRepository.findByEmail(email)
              .orElseThrow(() -> new ResourceNotFoundException("Email not exists"));

    // Tính toán thời gian hết hạn của token
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + jwtforgotPasswordExpirationMs);

    // Tạo JWT token với các thông tin cần thiết
    return Jwts.builder()
        .setSubject(employee.getUserName()) // Thiết lập subject của token là tên đăng nhập của người dùng
        .setIssuedAt(now) // Thiết lập thời gian phát hành token là thời điểm hiện tại
        .setExpiration(expiryDate) // Thiết lập thời gian hết hạn của token
        .signWith(SignatureAlgorithm.HS512, jwtSecret) // Ký token bằng thuật toán HS512 và bí mật jwtSecret
        .compact(); // Hoàn thiện token
  }

  // Phương thức này trích xuất tên người dùng (subject) từ JWT token
  public String getUserNameFromJwtToken(String token) {
    // Phân tích và lấy thông tin từ token
    return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
  }

  // Phương thức này kiểm tra tính hợp lệ của JWT token
  public boolean validateJwtToken(String authToken) {
    try {
      // Phân tích token và kiểm tra tính hợp lệ
      Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
      return true; // Token hợp lệ
    } catch (SignatureException e) {
      // Token có chữ ký không hợp lệ
      logger.error("Invalid JWT signature: {}", e.getMessage());
    } catch (MalformedJwtException e) {
      // Token bị sai định dạng
      logger.error("Invalid JWT token: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      // Token đã hết hạn
      logger.error("JWT token is expired: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      // Token không được hỗ trợ
      logger.error("JWT token is unsupported: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      // Token rỗng hoặc không hợp lệ
      logger.error("JWT claims string is empty: {}", e.getMessage());
    }

    return false; // Token không hợp lệ
  }
}
