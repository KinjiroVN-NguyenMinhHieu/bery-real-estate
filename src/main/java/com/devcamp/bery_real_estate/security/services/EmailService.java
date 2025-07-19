package com.devcamp.bery_real_estate.security.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender; // Inject JavaMailSender để gửi email

    // Phương thức để gửi email
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); // Tạo đối tượng SimpleMailMessage
        message.setTo(to); // Đặt địa chỉ email người nhận
        message.setSubject(subject); // Đặt chủ đề của email
        message.setText(text); // Đặt nội dung của email
        javaMailSender.send(message); // Gửi email
    }
}

