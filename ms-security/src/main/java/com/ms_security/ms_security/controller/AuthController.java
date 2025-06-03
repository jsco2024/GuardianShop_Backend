package com.ms_security.ms_security.controller;

import com.ms_security.ms_security.persistence.entity.UserEntity;
import com.ms_security.ms_security.service.IAuthServices;
import com.ms_security.ms_security.service.IJWTUtilityService;
import com.ms_security.ms_security.service.IUserService;
import com.ms_security.ms_security.service.model.dto.ChangePasswordDto;
import com.ms_security.ms_security.service.model.dto.LoginDto;
import com.ms_security.ms_security.service.model.dto.ResponseErrorDto;
import com.nimbusds.jose.JOSEException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class for handling authentication-related operations.
 * This class provides endpoints for user registration and login.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final IAuthServices _authServices;
    private final IJWTUtilityService _jwtUtilityService;

    /**
     * Registers a new user.
     *
     * @param user the user entity containing the details of the new user
     * @return a ResponseEntity containing a ResponseDto with the registration status
     * @throws Exception if an error occurs during the registration process
     */
    @Operation(summary = "Register a new user", description = "Registers a new user and returns the registration status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User registered successfully", content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content(schema = @Schema(implementation = ResponseErrorDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = ResponseErrorDto.class)))
    })
    @PostMapping("/register")
    private ResponseEntity<ResponseErrorDto> register(@RequestBody UserEntity user) throws Exception {
        return new ResponseEntity<>(_authServices.register(user), HttpStatus.CREATED);
    }

    /**
     * Authenticates a user and generates a JWT token.
     *
     * @param loginRequest the login DTO containing the user's credentials
     * @return a ResponseEntity containing a HashMap with the JWT token or an error message
     * @throws Exception if an error occurs during the authentication process
     */
    @Operation(summary = "User login", description = "Authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful", content = @Content(schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized - invalid credentials", content = @Content(schema = @Schema(implementation = HashMap.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = @Content(schema = @Schema(implementation = HashMap.class)))
    })
    @PostMapping("/login")
    private ResponseEntity<HashMap<String, String>> login(@RequestBody LoginDto loginRequest) throws Exception {
        HashMap<String, String> login = _authServices.login(loginRequest);
        if (login.containsKey("jwt")) {
            return new ResponseEntity<>(login, HttpStatus.OK);
        }
        return new ResponseEntity<>(login, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Sends a password reset email to the specified user.
     *
     * @param requestBody the email address of the user requesting a password reset
     * @throws Exception if there is an error during the email sending process
     */
    @Operation(summary = "Request password reset", description = "Sends a password reset link to the user's email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset email sent successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid email address"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/reset-password")
    public ResponseEntity<String> sendPasswordResetEmail(@RequestBody Map<String, String> requestBody) throws Exception {
        String email = requestBody.get("email");
        _authServices.sendPasswordResetEmail(email);
        return new ResponseEntity<>("Password reset email sent.", HttpStatus.OK);
    }


    /**
     * Resets the user's password using the provided token and new password.
     *
     * @param changePasswordDto the data transfer object containing the new password and token
     * @throws Exception if there is an error during the password change process
     */
    @Operation(summary = "Change user password", description = "Resets the user's password.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password changed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid token"),
            @ApiResponse(responseCode = "400", description = "Invalid password or token"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordDto changePasswordDto) {
        try {
            _authServices.changePassword(changePasswordDto);
            return new ResponseEntity<>("Password changed successfully.", HttpStatus.OK);
        } catch (Exception e) {
            // Manejar el caso de token inválido
            if (e.getMessage().equals("Invalid token")) {
                return new ResponseEntity<>("Invalid token.", HttpStatus.UNAUTHORIZED);
            } else {
                return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
    }

    @Operation(summary = "Logout user", description = "Logs out the user and invalidates the JWT token.")
    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authorizationHeader) {
        // Extraer el token del encabezado
        String token = authorizationHeader.replace("Bearer ", "");

        // Revocar el token
        _authServices.logout(token);

        return new ResponseEntity<>("Logout successful.", HttpStatus.OK);
    }

    /**
     * Validates the provided JWT token.
     *
     * @param request a map containing the token to validate
     * @return ResponseEntity indicating whether the token is valid or not
     */
    @Operation(summary = "Validate JWT token", description = "Checks if the provided token is valid.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token is valid."),
            @ApiResponse(responseCode = "401", description = "Invalid token.")
    })
    @PostMapping("/validate-token")
    public ResponseEntity<String> validateToken(@RequestBody Map<String, String> request) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(400).body("Token is required.");
        }

        boolean isValid = _jwtUtilityService.validateToken(token);
        if (isValid) {
            return ResponseEntity.ok("Token is valid.");
        } else {
            return ResponseEntity.status(401).body("Invalid token.");
        }
    }
}
