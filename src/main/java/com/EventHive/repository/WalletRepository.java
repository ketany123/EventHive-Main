package com.EventHive.repository;

import com.EventHive.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository extends JpaRepository<Wallet,Long> {
    Wallet findByReferelCode(String referelCode);
}
