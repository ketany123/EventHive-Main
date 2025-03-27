package com.EventHive.controller;


import com.EventHive.dto.ApiResponseDto;
import com.EventHive.dto.AuthRequestDto;
import com.EventHive.dto.AuthResponseDto;
import com.EventHive.dto.SignupRequestDto;
import com.EventHive.service.AuthService;
import com.EventHive.service.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;


    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;

    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponseDto> signupUser(@RequestBody SignupRequestDto signupRequestDto){
        String token = authService.signupUser(signupRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AuthResponseDto.builder().token(token).build());
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthResponseDto> authenticateUser(@RequestBody AuthRequestDto authRequestDto){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(authRequestDto.getUsername(), authRequestDto.getPassword());
        authenticationManager.authenticate(authToken);
        String token = authService.authenticateUser(authRequestDto.getUsername());
        return ResponseEntity.ok(AuthResponseDto.builder().token(token).build());

    }



}
