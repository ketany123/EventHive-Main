package com.EventHive.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class WalletRequest {
    private String username;
    private String password;
    private int money;
}