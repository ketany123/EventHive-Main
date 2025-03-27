package com.EventHive.service;

import com.EventHive.dto.SignupRequestDto;
import com.EventHive.entity.User;
import com.EventHive.enums.Role;
import com.EventHive.exception.UserExistsException;
import com.EventHive.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import static com.EventHive.constant.ExceptionMessages.USER_EXISTS;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, JwtService jwtService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    public String signupUser(SignupRequestDto signupRequestDto) {

        if(userRepository.findByUsername(signupRequestDto.getUsername()).isPresent())
            throw new UserExistsException(USER_EXISTS, HttpStatus.BAD_REQUEST);

        User user = User.builder()
                .username(signupRequestDto.getUsername())
                .firstName(signupRequestDto.getFirstName())
                .lastName(signupRequestDto.getLastName())
                .password(bCryptPasswordEncoder.encode(signupRequestDto.getPassword()))
                .email(signupRequestDto.getEmail())
                .role(Role.ROLE_USER)
                .build();

        User createdUser = userRepository.save(user);
        return jwtService.generateToken(createdUser);
    }

    public String authenticateUser(String username) {
        return userRepository.findByUsername(username)
                .map(jwtService::generateToken)
                .orElseThrow(RuntimeException::new);

    }
}
