package com.codingshuttle.youtube.hospitalManagement.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    private final JwtAuthFilter jwtAuthFilter;

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
                        auth.requestMatchers("/public/**", "/auth/**","/swagger-ui/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
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

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
