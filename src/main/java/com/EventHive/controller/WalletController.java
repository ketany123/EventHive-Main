package com.EventHive.controller;



import com.EventHive.dto.WalletRequest;
import com.EventHive.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wallet")
public class WalletController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WalletService walletService;

    @PostMapping("/addMoney")
    public String addMoney(@RequestBody WalletRequest walletRequest){
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(walletRequest.getUsername(), walletRequest.getPassword());
        authenticationManager.authenticate(authToken);

        return walletService.addMoney(walletRequest);
    }
}

