package com.EventHive.service;

import com.EventHive.dto.WalletRequest;
import com.EventHive.entity.User;
import com.EventHive.entity.Wallet;
import com.EventHive.repository.UserRepository;
import com.EventHive.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class WalletService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    public String addMoney(WalletRequest walletRequest) {
        Optional<User> byUsername = userRepository.findByUsername(walletRequest.getUsername());
        if(byUsername.isEmpty()){
            return "User name not found";
        }
        User user=byUsername.get();
        Wallet wallet= user.getWallet();
        wallet.setAmount(wallet.getAmount()+walletRequest.getMoney());
        userRepository.save(user);
        return "added money uccesfully";
    }


}

