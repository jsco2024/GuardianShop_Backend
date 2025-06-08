package com.ms_security.ms_security.security;

import com.ms_security.ms_security.service.IJWTUtilityService;
import com.ms_security.ms_security.service.impl.consultations.PermissionConsultations;
import com.ms_security.ms_security.service.impl.consultations.RoleConsultations;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Security configuration for the application.
 * <p>
 * This class configures HTTP security and authentication, including:
 * <ul>
 *     <li>Configuration of security filters.</li>
 *     <li>Authorization rules for application routes.</li>
 *     <li>Session management policy.</li>
 *     <li>Handling of authentication-related exceptions.</li>
 * </ul>
 * </p>
 *
 * @see JWTAuthorizationFilter
 * @see IJWTUtilityService
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final IJWTUtilityService _jwtUtilityService;
    private final RoleConsultations _roleConsultations;
    private final PermissionConsultations _permissionConsultations;

    /**
     * Configures the security filter chain for HTTP.
     *
     * @param http The HTTP security configuration.
     * @return The configured security filter chain.
     * @throws Exception If an error occurs during configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JWTAuthorizationFilter jwtAuthorizationFilter = new JWTAuthorizationFilter(
                _jwtUtilityService,
                _roleConsultations,
                _permissionConsultations
        );
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:5173");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(source))
                .authorizeHttpRequests(authRequests ->
                        authRequests
                                .requestMatchers("/auth/**", "/form/addRecord", "/form/updateRecord", "/services/list/all", "/carts/findCartByUser", "/services/inventory").permitAll()
                                .requestMatchers("/permission/**", "/role/**", "/users/list/all", "/users/addRecord", "/users/updateRecord", "/users/list/roles")
                                .hasRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        }))
                .build();
    }

    /**
     * Configures the password encoder.
     *
     * @return The configured password encoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
