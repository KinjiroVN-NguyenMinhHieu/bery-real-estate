package com.devcamp.bery_real_estate.security.jwt;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import com.devcamp.bery_real_estate.security.services.BlackListingService;
import com.devcamp.bery_real_estate.security.services.UserDetailsServiceImpl;

// Lớp AuthTokenFilter kế thừa từ OncePerRequestFilter để xử lý việc xác thực JWT cho mỗi yêu cầu HTTP
public class AuthTokenFilter extends OncePerRequestFilter {
  
  // Inject đối tượng JwtUtils để xử lý JWT
  @Autowired
  private JwtUtils jwtUtils;

  // Inject đối tượng UserDetailsServiceImpl để tải thông tin chi tiết người dùng từ username
  @Autowired
  private UserDetailsServiceImpl userDetailsService;
  
  // Inject BlackListingService để xử lý logout
  @Autowired
  @Lazy
  private BlackListingService blackListingService;

  // Logger để ghi log thông tin và lỗi
  private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

  // Phương thức này được gọi cho mỗi yêu cầu HTTP để thực hiện việc lọc và xác thực JWT
  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      // Lấy JWT từ yêu cầu HTTP
      String jwt = parseJwt(request);
      
      // Kiểm tra nếu JWT không rỗng và hợp lệ
      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
        // Lấy username từ JWT
        String username = jwtUtils.getUserNameFromJwtToken(jwt);

        // Kiểm tra xem token có trong danh sách đen hay không
        if (blackListingService.isTokenBlacklisted(jwt, username)) {
          // Token nằm trong danh sách đen, từ chối truy cập
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token is blacklisted");
          return;
        }

        // Tải thông tin chi tiết người dùng từ username
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        // Tạo đối tượng UsernamePasswordAuthenticationToken với thông tin chi tiết người dùng và quyền hạn
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities());
        
        // Thiết lập chi tiết xác thực cho đối tượng authentication
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // Đặt đối tượng authentication vào SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      // Ghi log lỗi nếu không thể thiết lập xác thực người dùng
      logger.error("Cannot set user authentication: {}", e);
    }

    // Tiếp tục chuỗi lọc (filter chain) cho yêu cầu
    filterChain.doFilter(request, response);
  }

  // Phương thức này lấy JWT từ tiêu đề Authorization của yêu cầu HTTP
  public String parseJwt(HttpServletRequest request) {
    // Lấy giá trị của tiêu đề Authorization
    String headerAuth = request.getHeader("Authorization");

    // Kiểm tra nếu tiêu đề Authorization có giá trị và bắt đầu với "Bearer "
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
      // Trả về JWT bằng cách cắt chuỗi "Bearer " khỏi tiêu đề
      return headerAuth.substring(7, headerAuth.length());
    }

    // Trả về null nếu không tìm thấy JWT
    return null;
  }
}
