package com.codingshuttle.youtube.hospitalManagement.controller;

import com.codingshuttle.youtube.hospitalManagement.entity.LoginRequestDto;
import com.codingshuttle.youtube.hospitalManagement.entity.LoginResponseDto;
import com.codingshuttle.youtube.hospitalManagement.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto loginRequestDto){
        //LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(authService.login(loginRequestDto));
    }

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDto> signup(@RequestBody LoginRequestDto signupRequestDto){
        //LoginResponseDto loginResponseDto = authService.login(loginRequestDto);
        return ResponseEntity.ok(authService.signup(signupRequestDto));
    }

}
