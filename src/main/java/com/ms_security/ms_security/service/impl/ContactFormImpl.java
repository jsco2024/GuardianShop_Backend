package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.ContactFormEntity;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.IContactFormService;
import com.ms_security.ms_security.service.IEmailService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.ContactFormConsultations;
import com.ms_security.ms_security.service.model.dto.ContactFormDto;
import com.ms_security.ms_security.service.model.dto.EmailDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ContactFormImpl implements IContactFormService {

    private final ContactFormConsultations _contactFormConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;
    private final IEmailService _iEmailService;
    /**
     * Method responsible for searching for a record by the RequestDetail ID.
     *
     * @param encode References the request encoded in base64.
     * @return Returns a ResponseEntity object with the requested data if found, otherwise an error message.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        ContactFormDto findByIdDto = EncoderUtilities.decodeRequest(encode, ContactFormDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<ContactFormEntity> contactFormEntity = _contactFormConsultations.findById(findByIdDto.getId());
        if(contactFormEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        ContactFormEntity contacformEntity = contactFormEntity.get();
        ContactFormDto contactFormDtos = parse(contacformEntity);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(contactFormDtos, 1L);
    }

    /**
     * Lists all records with pagination.
     *
     * @param encode Refers to the request encoded in base64.
     * @return Returns a ResponseEntity object with a paginated list of DTOs if records are found, otherwise an error message.
     */
    @Override
    public ResponseEntity<String> findAll(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INITIATING PAGINATED SEARCH");
        FindByPageDto request = EncoderUtilities.decodeRequest(encode, FindByPageDto.class);
        EncoderUtilities.validator(request);
        log.info(EncoderUtilities.formatJson(request));
        log.info("INITIATING PARAMETER QUERY");
        Optional<ParametersEntity> pageSizeParam = _iParametersService.findByCodeParameter(1L);
        log.info("PARAMETER QUERY COMPLETED");
        Pageable pageable = PageRequest.of(
                request.getPage() - 1,
                Integer.parseInt(pageSizeParam.get().getParameter()));
        Page<ContactFormEntity> pageResult = _contactFormConsultations.findAll(pageable);
        List<ContactFormDto> contactFormDtoList = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<ContactFormDto> response = new PageImpl<>(contactFormDtoList, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Method responsible for creating a new record.
     *
     * @param encode Refers to the request encoded in base64.
     * @return Returns a ResponseEntity object with the created record if successful, otherwise an error message if a record with the same email already exists.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        ContactFormDto contactFormDto = EncoderUtilities.decodeRequest(encode, ContactFormDto.class);
        EncoderUtilities.validator(contactFormDto, ContactFormDto.Create.class);
        log.info(EncoderUtilities.formatJson(contactFormDto));
        log.info("START SEARCH BY EMAIL");
        Optional<ContactFormEntity> existingForm = _contactFormConsultations.findByEmail(contactFormDto.getEmail());
        if (existingForm.isPresent()) return _errorControlUtilities.handleSuccess(null, 10L);
        log.info("END SEARCH BY EMAIL");
        ContactFormEntity existingEntity = parseEnt(contactFormDto, new ContactFormEntity());
        existingEntity.setDateTimeReceived(new Date().toString());
        ContactFormEntity contactFormEntity = _contactFormConsultations.addNew(existingEntity);
        ContactFormDto contactFormDtos = parse(contactFormEntity);
        log.info("INSERT ENDED");
        try {
            sendNotificationEmail(contactFormDtos);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
            return _errorControlUtilities.handleSuccess(null, 12L);
        }
        return _errorControlUtilities.handleSuccess(contactFormDtos, 1L);
    }

    /**
     * Method responsible for updating a record.
     *
     * @param encode Refers to the request encoded in base64.
     * @return Returns a ResponseEntity object with the updated record if successful, otherwise an error message if the record does not exist or the email does not match.
     */
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        ContactFormDto contactFormDto = EncoderUtilities.decodeRequest(encode, ContactFormDto.class);
        EncoderUtilities.validator(contactFormDto, ContactFormDto.Update.class);
        log.info(EncoderUtilities.formatJson(contactFormDto));
        log.info("START SEARCH BY ID");
        Optional<ContactFormEntity> contactForm = _contactFormConsultations.findById(contactFormDto.getId());
        if (contactForm.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        log.info("END SEARCH BY ID");
        log.info("START SEARCH EMAIL");
        ContactFormEntity contactFormEntity = contactForm.get();
        if (!contactFormEntity.getEmail().equals(contactFormDto.getEmail())) return _errorControlUtilities.handleSuccess(null, 11L);
        log.info("END SEARCH EMAIL");
        ContactFormEntity existingEntity = parseEnt(contactFormDto, new ContactFormEntity());
        existingEntity.setDateTimeReceived(new Date().toString());
        ContactFormEntity updatedEntity = _contactFormConsultations.updateData(existingEntity);
        ContactFormDto updatedDto = parse(updatedEntity);
        log.info("INSERT ENDED");
        try {
            sendNotificationEmail(updatedDto);
        } catch (MessagingException e) {
            log.error("Error sending email: {}", e.getMessage());
            return _errorControlUtilities.handleSuccess(null, 12L);
        }
        return _errorControlUtilities.handleSuccess(updatedDto, 1L);
    }


    /**
     * Method for converting a `ContactFormEntity` object to a `ContactFormDto` object.
     *
     * @param entity The `ContactFormEntity` object to be converted.
     * @return Returns a `ContactFormDto` object with data from the entity.
     */
    private ContactFormDto parse(ContactFormEntity entity){
        ContactFormDto contactFormDto = new ContactFormDto();
        contactFormDto.setId(entity.getId());
        contactFormDto.setName(entity.getName());
        contactFormDto.setLastName(entity.getLastName());
        contactFormDto.setEmail(entity.getEmail());
        contactFormDto.setPhone(entity.getPhone());
        contactFormDto.setStatus(entity.getStatus());
        contactFormDto.setMessage(entity.getMessage());
        return contactFormDto;
    }

    /**
     * Method for converting a `ContactFormDto` object to a `ContactFormEntity` object.
     *
     * @param dto The `ContactFormDto` object to be converted.
     * @param entity The `ContactFormEntity` object to be updated.
     * @return Returns a `ContactFormEntity` object with data from the DTO.
     */
    private ContactFormEntity parseEnt(ContactFormDto dto, ContactFormEntity entity){
        ContactFormEntity contactFormEntity = new ContactFormEntity();
        contactFormEntity.setId(dto.getId());
        contactFormEntity.setName(dto.getName());
        contactFormEntity.setLastName(dto.getLastName());
        contactFormEntity.setPhone(dto.getPhone());
        contactFormEntity.setEmail(dto.getEmail());
        contactFormEntity.setMessage(dto.getMessage());
        contactFormEntity.setStatus(dto.getStatus());
        contactFormEntity.setDateTimeReceived(entity.getDateTimeReceived());
        return contactFormEntity;
    }

    private void sendNotificationEmail(ContactFormDto contactFormDto) throws MessagingException {
        EmailDto emailDtoToClient = new EmailDto();
        emailDtoToClient.setRecipient(contactFormDto.getEmail());
        emailDtoToClient.setSubject("Confirmation of request received");
        emailDtoToClient.setMessage(String.format("Hello %s %s,\n\nThank you for contacting us. We have received your request, One of our advisors will contact you shortly.",
                contactFormDto.getName(), contactFormDto.getLastName()));
        _iEmailService.sendEmail(emailDtoToClient, "contactConfirmation");
        log.info("Email sent successfully to client: {}", contactFormDto.getEmail());
        EmailDto emailDtoToAdmin = new EmailDto();
        emailDtoToAdmin.setRecipient("americandevopsinnovation@gmail.com");
        emailDtoToAdmin.setSubject("NEW FORM RECEIVED");
        emailDtoToAdmin.setMessage(String.format("A new form has been submitted by Cliente: %s %s, Email: %s, Phone: %s.\n\nMessage:\n%s",
                contactFormDto.getName(), contactFormDto.getLastName(), contactFormDto.getEmail(), contactFormDto.getPhone(), contactFormDto.getMessage()));
        _iEmailService.sendEmail(emailDtoToAdmin, "contactConfirmation");
        log.info("Notification email sent successfully to admin.");
    }


}
