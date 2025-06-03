package com.ms_security.ms_security.controller;


import com.ms_security.ms_security.service.IEmailService;
import com.ms_security.ms_security.service.model.dto.EmailDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
@RequiredArgsConstructor
public class EmailController {

    private final IEmailService _iEmailService;

    /**
     * Sends an email with the provided email data.
     *
     * @param email the email data to send, including recipient, subject, and body
     * @return a ResponseEntity indicating whether the email was sent successfully
     * @throws MessagingException if there is an error while sending the email
     */
    @Operation(summary = "Send an Email", description = "Sends an email using the provided email data (recipient, subject, body, etc.)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Email Sent Successfully", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/send")
    private ResponseEntity<String> sendEmail(@RequestBody EmailDto email, @RequestParam String templateName) throws MessagingException {
        _iEmailService.sendEmail(email, templateName);
        return new ResponseEntity<>("Email Sended Successfully", HttpStatus.OK);
    }
}
