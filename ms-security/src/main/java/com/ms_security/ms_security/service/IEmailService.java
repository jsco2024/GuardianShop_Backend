package com.ms_security.ms_security.service;

import com.ms_security.ms_security.service.model.dto.EmailDto;
import jakarta.mail.MessagingException;

/**
 * Interface defining the contract for sending emails.
 * Any class implementing this interface should provide an implementation for sending an email.
 */
public interface IEmailService {

    /**
     * Sends an email to the recipient specified in the EmailDto.
     *
     * @param email An instance of EmailDto containing the recipient's email address, subject, and message content.
     * @throws MessagingException If there is an error in creating or sending the email.
     */
    public void sendEmail(EmailDto email, String templateName) throws MessagingException;
}
