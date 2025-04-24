package org.ai.appointmentbackend.configuration;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Public endpoints
                        .requestMatchers(
                                "/api/login",
                                "/api/auth/patient",
                                "/api/forgotpassword/send-otp",
                                "/api/forgotpassword/verify-otp",
                                "/api/forgotpassword/reset-password"

                        ).permitAll()

                        //1st one sometimes delete one
                        .requestMatchers(HttpMethod.POST, "/api/auth/register-patient").permitAll()

                        .requestMatchers(HttpMethod.GET, "/api/doctors").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/doctors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/departments").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/departments/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/admin/login").permitAll()



                        // Patient-specific endpoints

                        .requestMatchers(HttpMethod.PUT, "/api/patients/{id}").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/patients/{id}").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/patients/{id}/medical-records").hasAnyRole("PATIENT", "ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/patients/{id}/appointments").hasAnyRole("PATIENT", "ADMIN")

                        // Appointment endpoints
                        .requestMatchers(HttpMethod.GET, "/api/appointments").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/appointments").hasRole("PATIENT")
                        .requestMatchers(HttpMethod.PUT, "/api/appointments/{id}/reschedule").hasAnyRole("PATIENT", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/appointments/{id}").hasAnyRole("PATIENT", "ADMIN","DOCTOR")
                        .requestMatchers(HttpMethod.GET, "/api/doctors/{id}/appointments").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/appointments?status={status}").hasRole("ADMIN")

                        // Doctor endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/doctor/update-profile").hasAnyRole("DOCTOR","ADMIN")
                        .requestMatchers(HttpMethod.GET,"/api/doctor/appointments").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/doctors/{id}").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/doctors/patientappointments/**").hasRole("PATIENT")
                        // Admin-only endpoints
                        .requestMatchers(HttpMethod.PUT,"/api/doctors/{doctorId}/availability").hasAnyRole("DOCTOR", "ADMIN")
                        .requestMatchers("/api/admin/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/api/doctors/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/patients/{id}").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/patients").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/admin/medical-records").hasAnyRole("ADMIN", "DOCTOR")
                        .requestMatchers(HttpMethod.PUT, "/api/admin/medical-records/{id}").hasAnyRole("ADMIN", "DOCTOR")


                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
     return new CorsConfigurationSource() {
         @Override
         public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
             CorsConfiguration config = new CorsConfiguration();
             config.setAllowCredentials(true);
             config.setAllowedOrigins(Arrays.asList("http://localhost:5173","http://localhost:5174"));
             config.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
             config.setMaxAge(3600L);
             config.setAllowedHeaders(Collections.singletonList("*"));
             config.setExposedHeaders(Collections.singletonList("Authorization"));
             return config;
         }

     };
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
     return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
     return configuration.getAuthenticationManager();
    }


}
