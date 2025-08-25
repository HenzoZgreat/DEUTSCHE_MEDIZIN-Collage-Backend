package Henok.example.DeutscheCollageBack_endAPI.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("*")); // Allow all origins
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // Allowed HTTP methods
                    config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
                    config.setAllowCredentials(false); // Credentials not needed for public APIs
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login", "/api/register").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/country/**",
                                "/api/impairments/**",
                                "/api/program-modality/**",
                                "/api/region/**",
                                "/api/woreda/**",
                                "/api/zone/**",
                                "/api/academic-years/**",
                                "/api/departments/**",
                                "/api/enrollment-type/**").permitAll()

                        .requestMatchers("/api/batches/**",
                                "/api/class-years/**",
                                "/api/courses/**",
                                "/api/course-categories/**",
                                "/api/departments/**",
                                "/api/student-course-scores/**",
                                "/api/student-statuses",
                                "/api/semesters/**").hasRole("REGISTRAR")
                        .requestMatchers("/api/register/student").hasAnyRole("REGISTRAR", "VICE_DEAN", "STUDENT", "DEPARTMENT_HEAD")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}