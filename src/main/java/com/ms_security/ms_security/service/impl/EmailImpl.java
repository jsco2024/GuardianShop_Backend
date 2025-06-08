package com.ms_security.ms_security.service.impl;


import com.ms_security.ms_security.service.IEmailService;
import com.ms_security.ms_security.service.model.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Service class implementation for sending emails using Spring's JavaMailSender and Thymeleaf for email templates.
 * This class provides the functionality to send an email with a given subject, recipient, and message content.
 */

@Service
@RequiredArgsConstructor
public class EmailImpl implements IEmailService {
    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    /**
     * Sends an email to a specified recipient with the given subject and message content.
     * The email content is processed using a Thymeleaf template called "email".
     *
     * @param email An instance of EmailDto containing recipient email address, subject, and message content.
     * @throws MessagingException If there is an error creating or sending the email.
     */
    @Override
    public void sendEmail(EmailDto email, String templateName) throws MessagingException {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email.getRecipient());
            helper.setSubject(email.getSubject());
            Context context = new Context();
            context.setVariable("message", email.getMessage());
            context.setVariable("resetLink", email.getResetLink());
            String contentHTML = templateEngine.process(templateName, context);
            helper.setText(contentHTML, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error Sending Email: " + e.getMessage(), e);
        }
    }


}
