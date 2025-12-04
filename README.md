# Bery Real Estate Platform

Nền tảng đăng tin bất động sản được xây dựng bằng **Spring Boot**, hỗ trợ đầy đủ các tính năng cần thiết cho người dùng, quản trị viên và nhà phát triển.

## 🚀 Tính năng chính

- 🔍 **Tìm kiếm nâng cao**: Lọc tin đăng theo khu vực, giá, loại bất động sản, diện tích,...
- 📝 **Đăng và quản lý tin**: Người dùng có thể đăng tin, chỉnh sửa, xoá, xem danh sách tin đã đăng và đánh dấu tin yêu thích.
- 👥 **Phân quyền người dùng**: Hệ thống phân vai trò rõ ràng giữa người dùng, môi giới và quản trị viên.
- 📊 **Trang quản trị**: Quản lý người dùng, phê duyệt tin đăng và xem biểu đồ thống kê theo thời gian.
- 🔐 **Khôi phục mật khẩu**: Gửi email chứa liên kết đặt lại mật khẩu khi người dùng quên.
- 🖼️ **Hỗ trợ upload ảnh**: Cho phép người dùng tải ảnh lên để hiển thị kèm tin đăng.

## ⚙️ Công nghệ sử dụng

- **Frontend**: HTML/CSS, JavaScript, jQuery, Bootstrap, AdminLTE
- **Backend**: Java 17, Spring Boot 2.6.7
- **Database**: MySQL (Spring Data JPA)
- **Security**: Spring Security, JWT (jjwt)
- **Cache**: Redis
- **Email**: JavaMailSender
- **File Upload**: MultipartFile (lưu trữ nội bộ)

## 🔧 Cấu hình môi trường

Tất cả thông số cấu hình (Redis, Email, JWT, Database, v.v.) được quản lý qua `application.properties` hoặc biến môi trường trong file `.env`.

> 📌 Bạn có thể tự điều chỉnh các thông số phù hợp với môi trường của mình. Xem chi tiết trong mã nguồn.
