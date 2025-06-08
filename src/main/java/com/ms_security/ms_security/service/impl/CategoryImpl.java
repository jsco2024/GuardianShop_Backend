package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.CategoryEntity;
import com.ms_security.ms_security.persistence.entity.ParametersEntity;
import com.ms_security.ms_security.service.ICategoryService;
import com.ms_security.ms_security.service.IParametersService;
import com.ms_security.ms_security.service.impl.consultations.CategoryConsultations;
import com.ms_security.ms_security.service.model.dto.CategoryDto;
import com.ms_security.ms_security.service.model.dto.FindByPageDto;
import com.ms_security.ms_security.utilities.EncoderUtilities;
import com.ms_security.ms_security.utilities.ErrorControlUtilities;
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
public class CategoryImpl implements ICategoryService {

    private final CategoryConsultations _categoryConsultations;
    private final IParametersService _iParametersService;
    private final ErrorControlUtilities _errorControlUtilities;

    /**
     * Busca una categoría por su ID.
     *
     * @param encode el ID de la categoría codificado en base64.
     * @return una respuesta que contiene la categoría encontrada o un mensaje de error.
     */
    @Override
    public ResponseEntity<String> findById(String encode) {
        log.info("PROCEED TO SEARCH BY ID");
        EncoderUtilities.validateBase64(encode);
        CategoryDto findByIdDto = EncoderUtilities.decodeRequest(encode, CategoryDto.class);
        EncoderUtilities.validator(findByIdDto);
        log.info(EncoderUtilities.formatJson(findByIdDto));
        Optional<CategoryEntity> categoryEntity = _categoryConsultations.findById(findByIdDto.getId());
        if(categoryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        CategoryEntity category = categoryEntity.get();
        CategoryDto categoryDto = parse(category);
        log.info("SEARCH BY ID IS ENDED");
        return _errorControlUtilities.handleSuccess(categoryDto, 1L);
    }

    /**
     * Busca todas las categorías con paginación.
     *
     * @param encode parámetros de paginación codificados en base64.
     * @return una respuesta que contiene las categorías paginadas o un mensaje de error.
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
        Page<CategoryEntity> pageResult = _categoryConsultations.findAll(pageable);
        List<CategoryDto> categoryDtoList = pageResult.stream()
                .map(this::parse)
                .toList();
        PageImpl<CategoryDto> response = new PageImpl<>(categoryDtoList, pageable, pageResult.getTotalElements());
        log.info("PAGINATED SEARCH COMPLETED");
        return _errorControlUtilities.handleSuccess(response, 1L);
    }


    /**
     * Agrega una nueva categoría.
     *
     * @param encode la categoría a agregar codificada en base64.
     * @return una respuesta que contiene la categoría agregada o un mensaje de error.
     */
    @Override
    public ResponseEntity<String> addNew(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("INSERT BEGINS");
        CategoryDto categoryDto = EncoderUtilities.decodeRequest(encode, CategoryDto.class);
        EncoderUtilities.validator(categoryDto, CategoryDto.Create.class);
        log.info(EncoderUtilities.formatJson(categoryDto));
        log.info("START SEARCH BY NAME");
        Optional<CategoryEntity> name = _categoryConsultations.findByName(categoryDto.getName());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 14L);
        log.info("END SEARCH BY NAME");
        CategoryEntity existingEntity = parseEnt(categoryDto, new CategoryEntity());
        existingEntity.setCreateUser(categoryDto.getCreateUser());
        existingEntity.setDateTimeCreation(new Date().toString());
        CategoryEntity categoryEntity = _categoryConsultations.addNew(existingEntity);
        CategoryDto categoryDtos = parse(categoryEntity);
        log.info("INSERT ENDED");
        return _errorControlUtilities.handleSuccess(categoryDtos, 1L);
    }

    /**
     * Actualiza una categoría existente.
     *
     * @param encode la categoría con datos actualizados codificada en base64.
     * @return una respuesta que contiene la categoría actualizada o un mensaje de error.
     */
    @Override
    public ResponseEntity<String> updateData(String encode) {
        EncoderUtilities.validateBase64(encode);
        log.info("UPDATE BEGINS");
        CategoryDto categoryDto = EncoderUtilities.decodeRequest(encode, CategoryDto.class);
        EncoderUtilities.validator(categoryDto, CategoryDto.Update.class);
        log.info(EncoderUtilities.formatJson(categoryDto));
        log.info("START SEARCH BY ID");
        Optional<CategoryEntity> categoryEntity = _categoryConsultations.findById(categoryDto.getId());
        if (categoryEntity.isEmpty()) return _errorControlUtilities.handleSuccess(null, 3L);
        Optional<CategoryEntity> name = _categoryConsultations.findByName(categoryDto.getName());
        if (name.isPresent()) return _errorControlUtilities.handleSuccess(null, 14L);
        log.info("START SEARCH BY ID");
        CategoryEntity existingEntity = parseEnt(categoryDto, categoryEntity.get());
        existingEntity.setUpdateUser(categoryDto.getUpdateUser());
        existingEntity.setDateTimeUpdate(new Date().toString());
        CategoryEntity updatedCategory = _categoryConsultations.updateData(existingEntity);
        CategoryDto categoryDtos = parse(updatedCategory);
        log.info("UPDATE ENDED");
        return _errorControlUtilities.handleSuccess(categoryDtos, 1L);
    }

    /**
     * Convierte una entidad de categoría en un DTO de categoría.
     *
     * @param entity la entidad de categoría a convertir.
     * @return el DTO de categoría resultante.
     */
    private CategoryDto parse(CategoryEntity entity) {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(entity.getId());
        categoryDto.setName(entity.getName());
        categoryDto.setDescription(entity.getDescription());
        categoryDto.setStatus(entity.getStatus());
        categoryDto.setCreateUser(entity.getCreateUser());
        categoryDto.setUpdateUser(entity.getUpdateUser());
        return categoryDto;
    }

    /**
     * Convierte un DTO de categoría en una entidad de categoría.
     *
     * @param dto    el DTO de categoría a convertir.
     * @param entity la entidad de categoría existente que se actualizará.
     * @return la entidad de categoría actualizada.
     */
    private CategoryEntity parseEnt(CategoryDto dto, CategoryEntity entity) {
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setStatus(dto.getStatus());
        entity.setCreateUser(entity.getCreateUser());
        entity.setUpdateUser(entity.getUpdateUser());
        entity.setDateTimeCreation(entity.getDateTimeCreation());
        entity.setDateTimeUpdate(entity.getDateTimeUpdate());
        return entity;
    }
}
