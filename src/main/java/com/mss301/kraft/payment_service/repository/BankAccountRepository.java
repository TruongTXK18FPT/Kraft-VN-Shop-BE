package com.mss301.kraft.payment_service.repository;

import com.mss301.kraft.payment_service.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BankAccountRepository extends JpaRepository<BankAccount, UUID> {
    
    List<BankAccount> findByIsActiveTrue();
    
    boolean existsByAccountNumber(String accountNumber);
    
    boolean existsByAccountNumberAndIdNot(String accountNumber, UUID id);
}

