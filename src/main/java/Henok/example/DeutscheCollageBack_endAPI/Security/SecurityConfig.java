package Henok.example.DeutscheCollageBack_endAPI.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtRequestFilter jwtRequestFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList("*")); // Allow all origins
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")); // Allowed HTTP methods
                    config.setAllowedHeaders(Arrays.asList("*")); // Allow all headers
                    config.setAllowCredentials(false); // Credentials not needed for public APIs
                    return config;
                }))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/api/auth/login",
                                "/api/applicants/register", "/api/auth/register", "/").permitAll()
                        .requestMatchers(HttpMethod.GET,
                                "/api/enums/**",
                                "/api/country/**",
                                "/api/impairments/**",
                                "/api/program-modality/**",
                                "/api/program-levels/**",
                                "/api/region/**",
                                "/api/woreda/**",
                                "/api/zone/**",
                                "/api/academic-years/**",
                                "/api/departments/**",
                                "/api/enrollment-type/**",
                                "/api/school-backgrounds/**",
                                "/api/class-years/**",
                                "/api/semesters/**",
                                "/api/filters/options").permitAll()

                        .requestMatchers("/api/students/profile").hasRole("STUDENT")

                        .requestMatchers(HttpMethod.GET, "/api/students/**").hasAnyRole("DEPARTMENT_HEAD", "REGISTRAR", "TEACHER", "DEAN", "VICE_DEAN", "GENERAL_MANAGER")


                        // Registrar endpoints
                        .requestMatchers(
                                "/api/batches/**",
                                "/api/class-years/**",
                                "/api/courses/**",
                                "/api/course-categories/**",
                                "/api/course-sources/**",
                                "/api/student-course-scores/**",
                                "/api/student-statuses/**",
                                "/api/semesters/**",
                                "/api/grading-systems/**",
                                "/api/mark-intervals/**",
                                "/api/bcsy/**",
                                "/api/applicants/**",
                                "/api/auth/registrar/students/*/reset-password",
                                "/api/auth/register/student").hasRole("REGISTRAR")
                        .requestMatchers("/api/courses/**").hasAnyRole("DEPARTMENT_HEAD", "REGISTRAR")
                        .requestMatchers(
                                "/api/departments/**",
                                "/api/program-levels/**",
                                "/api/program-modality").hasAnyRole("DEAN", "VICE_DEAN", "REGISTRAR")
                        .requestMatchers("/api/auth/register/registrar").hasAnyRole("GENERAL_MANAGER", "VICE_DEAN")
                        .requestMatchers("/api/auth/register/general-manager").hasAnyRole("GENERAL_MANAGER")
                        
                        // Department Head endpoints
                        .requestMatchers("/api/auth/register/teacher",
                                "/api/auth/head/teachers/*/reset-password",
                                "/api/department-heads/profile",
                                "/api/department-heads/dashboard",
                                "/api/department-heads/profile/photo",
                                "/api/department-heads/profile/document",
                                "/api/department-heads/teachers",
                                "/api/department-heads/my-courses",
                                "api/department-heads/my-students",
                                "/api/department-heads/assessments/scores",
                                "/api/department-heads/assessments/*/approve").hasRole("DEPARTMENT_HEAD")
                        .requestMatchers(HttpMethod.PUT,"/api/teachers/**").hasRole("DEPARTMENT_HEAD")
                        .requestMatchers(HttpMethod.DELETE,
                                "/api/teachers/**",
                                "/api/teachers/{teacherId}/course-assignments/**").hasRole("DEPARTMENT_HEAD")
                        .requestMatchers(HttpMethod.POST, "/api/teachers/{teacherId}/course-assignments").hasRole("DEPARTMENT_HEAD")

                        
                        // Teacher endpoints
                        .requestMatchers("/api/teachers/profile",
                                       "/api/teachers/update",
                                       "/api/teachers/my-students",
                                       "/api/teachers/courses/*/students",
                                       "/api/teachers/my-courses",
                                       "/api/teachers/dashboard",
                                       "/api/assessments/**", 
                                       "/api/student-assessments/**").hasRole("TEACHER")

                        .requestMatchers(HttpMethod.GET, 
                                "/api/auth/me", 
                                "/api/auth/me/change-password").authenticated()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)  // ← This is key
//                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint()) // optional: for unauthenticated
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