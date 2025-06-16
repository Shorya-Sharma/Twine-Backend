package com.twine.service.impl;

import com.twine.constants.OtpConstants;
import com.twine.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Override
    public void sendOtpEmail(String to, String otp) throws MessagingException {
        try {
            Context context = createEmailContext(otp);
            String emailContent = templateEngine.process(OtpConstants.OTP_EMAIL_TEMPLATE, context);
            MimeMessage message = createMimeMessage(to, emailContent);
            mailSender.send(message);
            log.info("OTP email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to: {}", to, e);
            throw new MessagingException("Failed to send OTP email: " + e.getMessage());
        }
    }

    private Context createEmailContext(String otp) {
        Context context = new Context();
        context.setVariable("otp", otp);
        return context;
    }

    private MimeMessage createMimeMessage(String to, String content) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(to);
        helper.setSubject(OtpConstants.EMAIL_SUBJECT);
        helper.setText(content, true);
        
        return message;
    }
} 