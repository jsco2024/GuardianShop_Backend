package com.ms_security.ms_security.service.impl;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.persistence.entity.RoleEntity;
import com.ms_security.ms_security.service.IJWTUtilityService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

/**
 * Service implementation for handling JWT utility operations.
 * Provides methods for generating and parsing JWT tokens using RSA keys.
 */
@Service
public class JWTUtilityServiceImpl implements IJWTUtilityService {

    @Value("classpath:jwtKeys/private_key.pem")
    private Resource privateKeyResource;

    @Value("classpath:jwtKeys/public_key.pem")
    private Resource publicKeyResource;

    /**
     * Generates a JWT token for the given user ID.
     *
     * @param userId the ID of the user to be included in the token
     * @param roles a set of roles assigned to the user
     * @param permissions a set of permissions assigned to the user
     * @return the generated JWT token as a string
     * @throws IOException if there is an error reading the private key file
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws InvalidKeySpecException if the private key specification is invalid
     * @throws JOSEException if there is an error generating the JWT
     */
    @Override
    public String generateJWT(Long userId, Set<RoleEntity> roles, Set<PermissionEntity> permissions, long expirationTime) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, JOSEException {
        PrivateKey privateKey = loadPrivateKey(privateKeyResource);

        JWSSigner signer = new RSASSASigner(privateKey);

        Date now = new Date();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(userId.toString())
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + expirationTime)) // Usa el tiempo de expiración pasado como argumento
                .claim("roles", roles.stream().map(RoleEntity::getName).toList())
                .claim("permissions", permissions.stream().map(PermissionEntity::getUrl).toList())
                .build();

        SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.RS256), claimsSet);
        signedJWT.sign(signer);

        return signedJWT.serialize();
    }




    /**
     * Parses and validates a JWT token.
     *
     * @param jwt the JWT token to be parsed
     * @return the JWTClaimsSet extracted from the token
     * @throws ParseException if there is an error parsing the token
     * @throws JOSEException if there is an error verifying the token or if the token is expired
     * @throws IOException if there is an error reading the public key file
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws InvalidKeySpecException if the public key specification is invalid
     */
    @Override
    public JWTClaimsSet parseJWT(String jwt) throws ParseException, JOSEException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        PublicKey publicKey = loadPublicKey(publicKeyResource);

        SignedJWT signedJWT = SignedJWT.parse(jwt);
        JWSVerifier verifier = new RSASSAVerifier((RSAPublicKey) publicKey);
        if (!signedJWT.verify(verifier)) {
            throw new JOSEException("Invalid signature");
        }

        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

        if (claimsSet.getExpirationTime().before(new Date())) {
            throw new JOSEException("Expired token");
        }

        return claimsSet;
    }

    /**
     * Loads a private key from the specified resource file.
     *
     * @param resource the resource containing the private key in PEM format
     * @return the loaded PrivateKey
     * @throws IOException if there is an error reading the key file
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws InvalidKeySpecException if the private key specification is invalid
     */
    private PrivateKey loadPrivateKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        String privateKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(decodedKey));
    }

    /**
     * Loads a public key from the specified resource file.
     *
     * @param resource the resource containing the public key in PEM format
     * @return the loaded PublicKey
     * @throws IOException if there is an error reading the key file
     * @throws NoSuchAlgorithmException if RSA algorithm is not available
     * @throws InvalidKeySpecException if the public key specification is invalid
     */
    private PublicKey loadPublicKey(Resource resource) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] keyBytes = Files.readAllBytes(Paths.get(resource.getURI()));
        String publicKeyPEM = new String(keyBytes, StandardCharsets.UTF_8)
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(new X509EncodedKeySpec(decodedKey));
    }

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
    public boolean validateToken(String token) throws JOSEException, IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            // Parse the token to verify its signature and claims
            parseJWT(token);
            return true; // If no exception is thrown, the token is valid
        } catch (JOSEException | ParseException e) {
            // Log the error if needed, e.g., logger.error("Token validation failed: {}", e.getMessage());
            return false; // Token is invalid
        }
    }

}
