package com.ms_security.ms_security.service;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Set;

/**
 * Interface for services related to JSON Web Tokens (JWT).
 * Provides methods for generating and parsing JWTs.
 */
public interface IJWTUtilityService {

    /**
     * Generates a JWT for a specific user with associated roles and permissions.
     *
     * @param userId the ID of the user for whom the JWT is generated
     * @param roles the roles assigned to the user
     * @param permissions the permissions granted to the user
     * @return the generated JWT as a string
     * @throws IOException if there is an issue reading the key files
     * @throws NoSuchAlgorithmException if the algorithm for key generation is not found
     * @throws InvalidKeySpecException if the key specification is invalid
     * @throws JOSEException if there is an error creating or signing the JWT
     */
    public String generateJWT(Long userId, Set<RoleEntity> roles, Set<PermissionEntity> permissions, long expirationTime) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException;

    /**
     * Parses and validates a JWT.
     *
     * @param jwt the JWT to parse
     * @return the claims contained in the JWT
     * @throws ParseException if the JWT cannot be parsed
     * @throws JOSEException if the JWT verification fails
     * @throws IOException if there is an issue reading the key files
     * @throws NoSuchAlgorithmException if the algorithm for key verification is not found
     * @throws InvalidKeySpecException if the key specification is invalid
     */
    JWTClaimsSet parseJWT(String jwt) throws ParseException, JOSEException, IOException, NoSuchAlgorithmException, InvalidKeySpecException;

    /**
     * Validates the given JWT token.
     *
     * @param token the JWT token to be validated
     * @return true if the token is valid; false otherwise
     * @throws JOSEException if there is an error verifying the token
     * @throws IOException if there is an error reading the public key file
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws InvalidKeySpecException if the public key specification is invalid
     */
    boolean validateToken(String token) throws JOSEException, IOException, NoSuchAlgorithmException, InvalidKeySpecException;
}
