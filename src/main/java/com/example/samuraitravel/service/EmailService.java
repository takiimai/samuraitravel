package com.example.samuraitravel.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.url}")
    private String appUrl;

    @Autowired
    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendVerificationEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("verificationUrl", appUrl + "/auth/verify-email?token=" + token);
        
        String content = templateEngine.process("email/verification", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("メールアドレスの確認");
        helper.setText(content, true);
        
        mailSender.send(message);
    }
    
    public void sendPasswordResetEmail(String to, String token) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        Context context = new Context();
        context.setVariable("resetUrl", appUrl + "/auth/reset-password?token=" + token);
        
        String content = templateEngine.process("email/password-reset", context);
        
        helper.setFrom(fromEmail);
        helper.setTo(to);
        helper.setSubject("パスワードのリセット");
        helper.setText(content, true);
        
        mailSender.send(message);
    }
}
