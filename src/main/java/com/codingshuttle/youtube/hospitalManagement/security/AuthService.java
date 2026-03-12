package com.codingshuttle.youtube.hospitalManagement.security;

import com.codingshuttle.youtube.hospitalManagement.controller.SignUpResponseDto;
import com.codingshuttle.youtube.hospitalManagement.entity.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final AuthUtil authUtil;
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDto.getUsername(),
                        loginRequestDto.getPassword()
                )
        );
        User user = (User)authentication.getPrincipal();

        String token = authUtil.generateAccessToken(user);
        return new LoginResponseDto(token, user.getId());
    }

    //signup from oauth2
    public User signUpInternal(LoginRequestDto signupRequestDto,ProviderType providerType, String providerId){
        User user = userRepository.findByUsername(signupRequestDto.getUsername()).orElse(null);
        if (user!=null) throw new IllegalArgumentException("User already exists");

       user =  User.builder().username(signupRequestDto.getUsername())
               .providerId(providerId)
               .providerType(providerType)
               .build();

       if (providerType == ProviderType.EMAIL){
           user.setPassword(passwordEncoder.encode(signupRequestDto.getPassword()));
       }
       return userRepository.save(user);

    }

    public SignUpResponseDto signup(LoginRequestDto signupRequestDto) {

       User user = signUpInternal(signupRequestDto,ProviderType.EMAIL,null);
       return new SignUpResponseDto(user.getId(), user.getUsername());

    }

    @Transactional
    public ResponseEntity<LoginResponseDto> handleOAuth2LoginRequest(OAuth2User oAuth2User, String registrationId) {
        // fetch providerType and providerId
        ProviderType providerType = authUtil.getProviderTypeFromRegistrationId(registrationId);
        String providerId = authUtil.determineProviderIdFromOAuth2User(oAuth2User, registrationId);
        User user = userRepository.findByProviderIdAndProviderType(providerId, providerType).orElse(null);

        String email = oAuth2User.getAttribute("email");
        User emailUser= userRepository.findByUsername(email).orElse(null);

        if (user == null || emailUser ==null){
            String username = authUtil.determineUsernameFromOAuth2User(oAuth2User, registrationId, providerId);
            user = signUpInternal(new LoginRequestDto(username, null),providerType,providerId);

        } else if (user!=null) {
            if(email!=null && !email.isBlank() && !email.equals(user.getUsername())){
                user.setUsername(email);
                userRepository.save(user);
            }


        }else{
            throw new BadCredentialsException("This user is already registered with provider "+email);
        }
        // save provider type and provider id info with the user
        // if the user has and an account: directly login
        // otherwise first signup and then login
        LoginResponseDto loginResponseDto = new LoginResponseDto(authUtil.generateAccessToken(user), user.getId());
        return ResponseEntity.ok(loginResponseDto);


    }
}
