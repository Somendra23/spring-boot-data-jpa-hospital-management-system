package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.entity.PermissionType;
import com.codingshuttle.youtube.hospitalManagement.entity.type.RoleType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
@Slf4j
@EnableMethodSecurity
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private final JwtAuthFilter jwtAuthFilter;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        //makes all request public , bypasses form login
        // httpSecurity.formLogin(Customizer.withDefaults());
        //now that we are using our own login we dont need the form login feature! this is what AuthController will handle with login and signup
        //end point
//        return httpSecurity.build();

//        httpSecurity.authorizeHttpRequests(auth->
//                auth.requestMatchers("/public/**").permitAll()
//                        .requestMatchers("/admin/**").authenticated());
//        return httpSecurity.build();
        //disable CSRF and SessionManagment to be stateless so you could use jwt
        httpSecurity
                .csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer.disable())
                .sessionManagement(sessionManagementConfigurer ->
                        sessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth ->
                        auth.requestMatchers("/public/**",
                                        "/auth/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html",
                                        "/v3/api-docs/**").permitAll()
                                .requestMatchers("/admin/**").hasRole(RoleType.ADMIN.name())
                                .requestMatchers("/doctors/**").hasAnyRole(RoleType.DOCTOR.name(), RoleType.ADMIN.name())
                                .requestMatchers("/patients/**").hasRole(RoleType.PATIENT.name())
                                //After ading permissions we can further control the access
                                .requestMatchers(HttpMethod.DELETE, "/admin/**").hasAnyAuthority(PermissionType.APPOINTMENT_DELETE.getPermission())
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
               //configures oAuth2 , this also automatically provides a login page, which you could provide
                .oauth2Login(auth2->auth2.failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        log.error("OAuth2 Authentication failed: {}", exception.getMessage());
                    }

               }).successHandler(oAuth2SuccessHandler)
//
//                .successHandler((request, response, authentication) -> {
//                    //google successfully authenticated now it will pass username, email, image etc. to success handler
//                        })

                );
        //Add my custom JWT authentication filter into the Spring Security chain, and make sure it runs before the
        // built-in username/password authentication filter.
        //Before Spring checks username/password login,first check whether this request has a JWT token.
//        Your jwtAuthFilter:
//        reads the Authorization header
//        extracts the JWT
//        validates it
//        loads the user
//        puts the authenticated user into SecurityContext
//        Request
//  ↓
//        JwtAuthFilter
//  ↓
//        UsernamePasswordAuthenticationFilter
//  ↓
//        authorization checks
//  ↓
//        controller

        return httpSecurity.build();
    }

    //using InMemoryUserDetailsManager
//    @Bean
    UserDetailsService userDetailsService() {
        UserDetails user1 = User.withUsername("admin").password(passwordEncoder.encode("admin")).roles("ADMIN").build();
        UserDetails user2 = User.withUsername("doctor").password(passwordEncoder.encode("doctor")).roles("DOCTOR").build();
        UserDetails user3 = User.withUsername("patient").password(passwordEncoder.encode("patient")).roles("PATIENT").build();
        return new InMemoryUserDetailsManager(user1, user2, user3);
    }

   }
