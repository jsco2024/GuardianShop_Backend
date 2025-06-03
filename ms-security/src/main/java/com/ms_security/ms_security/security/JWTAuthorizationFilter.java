package com.ms_security.ms_security.security;

import com.ms_security.ms_security.persistence.entity.PermissionEntity;
import com.ms_security.ms_security.service.IJWTUtilityService;
import com.ms_security.ms_security.service.impl.consultations.PermissionConsultations;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Filter for processing JWT authorization.
 * <p>
 * This filter extracts the JWT token from the request header, validates it, and sets the authentication in the security context.
 * It ensures that only requests with a valid JWT token are processed, otherwise it responds with a forbidden status.
 * </p>
 * <p>
 * The filter performs the following actions:
 * <ul>
 *     <li>Checks if the `Authorization` header is present and starts with "Bearer ". If not, the filter passes the request through.</li>
 *     <li>Extracts the token from the header and parses it to obtain claims.</li>
 *     <li>Maps the roles from the claims to Spring Security authorities.</li>
 *     <li>Creates an `UsernamePasswordAuthenticationToken` with the user details and authorities, and sets it in the `SecurityContextHolder`.</li>
 *     <li>Handles exceptions related to token parsing and validation by responding with a forbidden status and an error message.</li>
 * </ul>
 * </p>
 *
 * @see IJWTUtilityService
 */
@RequiredArgsConstructor
public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private final IJWTUtilityService _jwtUtilityService;
    private final RoleConsultations _roleConsultations;
    private final PermissionConsultations _permissionConsultations;

    /**
     * Processes the HTTP request and performs JWT authorization.
     * <p>
     * This method:
     * <ul>
     *     <li>Extracts the JWT token from the `Authorization` header.</li>
     *     <li>Validates the token and retrieves the claims.</li>
     *     <li>Maps roles from the token claims to Spring Security authorities.</li>
     *     <li>Sets the authentication in the `SecurityContextHolder`.</li>
     *     <li>Handles exceptions and sends an error response if the token is invalid or expired.</li>
     * </ul>
     * </p>
     *
     * @param request The HTTP request.
     * @param response The HTTP response.
     * @param filterChain The filter chain.
     * @throws ServletException If an error occurs during the processing of the request.
     * @throws IOException If an I/O error occurs while processing the request or response.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = header.substring(7);
        try {
            JWTClaimsSet claims = _jwtUtilityService.parseJWT(token);
            String username = claims.getSubject();
            String[] rolesArray = claims.getStringArrayClaim("roles");
            List<SimpleGrantedAuthority> authorities = Arrays.stream(rolesArray)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Agregar el prefijo "ROLE_"
                    .collect(Collectors.toList());


            // Crear el token de autenticación
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // Verificar permisos
            String requestUri = request.getRequestURI();
            String requestMethod = request.getMethod();  // Obtén el método HTTP de la solicitud
            boolean hasPermission = false;

            for (String role : rolesArray) {
                // Obtén los permisos del rol
                Set<PermissionEntity> permissions = _permissionConsultations.findPermissionsByRoleName(role);
                // Verifica si hay un permiso que coincida con la URI y el método
                hasPermission = permissions.stream().anyMatch(permission ->
                        permission.getUrl().equals(requestUri) &&
                                permission.getMethod().equalsIgnoreCase(requestMethod) // Asegúrate de comparar el método
                );

                if (hasPermission) {
                    break; // Si ya tiene permiso, sal del bucle
                }
            }

            // Si no tiene permiso, responde con un error 403
            if (!hasPermission) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access Denied: You do not have permission to access this resource");
                return;
            }

        } catch (ParseException | NoSuchAlgorithmException | InvalidKeySpecException | JOSEException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Invalid or expired token");
            return;
        }
        filterChain.doFilter(request, response);
    }

}