package com.EventHive.service;

import com.EventHive.dto.SignupRequestDto;
import com.EventHive.entity.User;
import com.EventHive.entity.Wallet;
import com.EventHive.enums.Role;
import com.EventHive.exception.UserExistsException;
import com.EventHive.repository.UserRepository;
import com.EventHive.repository.WalletRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    @Autowired
    private WalletRepository walletRepository;


    public String signupUser(SignupRequestDto signupRequestDto) {

        if(userRepository.findByUsername(signupRequestDto.getUsername()).isPresent())
            throw new UserExistsException(USER_EXISTS, HttpStatus.BAD_REQUEST);


        Wallet walletUser= new Wallet();
        if(signupRequestDto.getReferedBy()!=null){
            Wallet wallet= walletRepository.findByReferelCode(signupRequestDto.getReferedBy());
//    User referrer= wallet.getUser();
//    referrer.getWallet().
            if(wallet==null){
                return "Wrong Referel code";
            }
            walletUser.setReferedBy(signupRequestDto.getReferedBy());
            walletUser.setAmount(walletUser.getAmount()+50);


            wallet.setAmount(wallet.getAmount()+50);
            walletRepository.save(wallet);
        }


        walletUser.setReferelCode(generateReferralCode());


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
    private String generateReferralCode() {
        return "REF" +  UUID.randomUUID().toString().substring(0, 5).toUpperCase();
    }
}
