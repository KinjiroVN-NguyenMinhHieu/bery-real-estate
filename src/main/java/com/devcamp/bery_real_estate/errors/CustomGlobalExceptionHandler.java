package com.devcamp.bery_real_estate.errors;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.http.*;
import org.springframework.web.bind.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.*;
import org.springframework.web.servlet.mvc.method.annotation.*;

// Annotation này quản lý các exception được ném ra và cung cấp các xử lý toàn cục cho chúng
@ControllerAdvice
public class CustomGlobalExceptionHandler extends ResponseEntityExceptionHandler {

	// Xử lý lỗi cho @Valid
	@SuppressWarnings("null")
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// Tạo một map để chứa thông tin lỗi và các thuộc tính khác
		Map<String, Object> body = new LinkedHashMap<>();
		// Thêm timestamp vào map
		body.put("timestamp", new Date());
		// Thêm trạng thái vào map
		//body.put("status", status.value());
		// Lấy danh sách các lỗi
		List<String> errors = ex.getBindingResult().getFieldErrors().stream()
				// Sử dụng stream để lấy ra thông điệp lỗi của từng trường
				.map(x -> x.getDefaultMessage()).collect(Collectors.toList());
		// Thêm danh sách lỗi vào map
		body.put("errors", errors);

		// Trả về một ResponseEntity chứa map body, headers và status
		return new ResponseEntity<>(body, headers, status);
	}

}
