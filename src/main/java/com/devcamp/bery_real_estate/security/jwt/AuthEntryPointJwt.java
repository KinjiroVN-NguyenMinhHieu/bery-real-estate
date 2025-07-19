package com.devcamp.bery_real_estate.security.jwt;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {

  // Logger để ghi log thông tin và lỗi
  private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

  // Phương thức này được gọi khi một yêu cầu yêu cầu xác thực nhưng không được xác thực thành công
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
      throws IOException, ServletException {
    // Ghi log lỗi không xác thực được
    logger.error("Unauthorized error: {}", authException.getMessage());

    // Thiết lập kiểu dữ liệu của phản hồi là JSON
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    // Thiết lập mã trạng thái của phản hồi là 401 Unauthorized
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

    // Tạo một đối tượng Map để chứa thông tin lỗi
    final Map<String, Object> body = new HashMap<>();
    body.put("status", HttpServletResponse.SC_UNAUTHORIZED); // Thiết lập status là 401 Unauthorized
    body.put("error", "Unauthorized"); // Thiết lập thông báo lỗi là Unauthorized
    body.put("message", authException.getMessage()); // Lấy thông báo lỗi từ exception
    body.put("path", request.getServletPath()); // Lấy đường dẫn của yêu cầu

    // Tạo một ObjectMapper để chuyển đổi đối tượng Map thành dạng JSON và ghi vào OutputStream của phản hồi
    final ObjectMapper mapper = new ObjectMapper();
    mapper.writeValue(response.getOutputStream(), body);
  }

}
