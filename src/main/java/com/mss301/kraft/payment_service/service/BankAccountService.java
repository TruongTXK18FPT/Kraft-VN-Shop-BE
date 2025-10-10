package com.mss301.kraft.payment_service.service;

import com.mss301.kraft.common.exception.BadRequestException;
import com.mss301.kraft.common.exception.ResourceNotFoundException;
import com.mss301.kraft.payment_service.dto.BankAccountRequest;
import com.mss301.kraft.payment_service.dto.BankAccountResponse;
import com.mss301.kraft.payment_service.entity.BankAccount;
import com.mss301.kraft.payment_service.repository.BankAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;

    public List<BankAccountResponse> getAllBankAccounts() {
        return bankAccountRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<BankAccountResponse> getActiveBankAccounts() {
        return bankAccountRepository.findByIsActiveTrue()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public BankAccountResponse getBankAccountById(UUID id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));
        return convertToResponse(bankAccount);
    }

    @Transactional
    public BankAccountResponse createBankAccount(BankAccountRequest request) {
        // Check if account number already exists
        if (bankAccountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new BadRequestException("Số tài khoản đã tồn tại");
        }

        BankAccount bankAccount = BankAccount.builder()
                .bankName(request.getBankName())
                .accountNumber(request.getAccountNumber())
                .accountHolderName(request.getAccountHolderName())
                .imageUrl(request.getImageUrl())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        BankAccount savedBankAccount = bankAccountRepository.save(bankAccount);
        log.info("Created bank account: {} - {}", savedBankAccount.getBankName(), savedBankAccount.getAccountNumber());

        return convertToResponse(savedBankAccount);
    }

    @Transactional
    public BankAccountResponse updateBankAccount(UUID id, BankAccountRequest request) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        // Check if account number already exists (excluding current record)
        if (bankAccountRepository.existsByAccountNumberAndIdNot(request.getAccountNumber(), id)) {
            throw new BadRequestException("Số tài khoản đã tồn tại");
        }

        bankAccount.setBankName(request.getBankName());
        bankAccount.setAccountNumber(request.getAccountNumber());
        bankAccount.setAccountHolderName(request.getAccountHolderName());
        bankAccount.setImageUrl(request.getImageUrl());
        if (request.getIsActive() != null) {
            bankAccount.setIsActive(request.getIsActive());
        }

        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);
        log.info("Updated bank account: {} - {}", updatedBankAccount.getBankName(), updatedBankAccount.getAccountNumber());

        return convertToResponse(updatedBankAccount);
    }

    @Transactional
    public void deleteBankAccount(UUID id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        bankAccountRepository.delete(bankAccount);
        log.info("Deleted bank account: {} - {}", bankAccount.getBankName(), bankAccount.getAccountNumber());
    }

    @Transactional
    public BankAccountResponse toggleBankAccountStatus(UUID id) {
        BankAccount bankAccount = bankAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bank account not found"));

        bankAccount.setIsActive(!bankAccount.getIsActive());
        BankAccount updatedBankAccount = bankAccountRepository.save(bankAccount);
        
        log.info("Toggled bank account status: {} - {} (Active: {})", 
                updatedBankAccount.getBankName(), 
                updatedBankAccount.getAccountNumber(),
                updatedBankAccount.getIsActive());

        return convertToResponse(updatedBankAccount);
    }

    private BankAccountResponse convertToResponse(BankAccount bankAccount) {
        return BankAccountResponse.builder()
                .id(bankAccount.getId())
                .bankName(bankAccount.getBankName())
                .accountNumber(bankAccount.getAccountNumber())
                .accountHolderName(bankAccount.getAccountHolderName())
                .imageUrl(bankAccount.getImageUrl())
                .isActive(bankAccount.getIsActive())
                .createdAt(bankAccount.getCreatedAt() != null
                        ? bankAccount.getCreatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .updatedAt(bankAccount.getUpdatedAt() != null
                        ? bankAccount.getUpdatedAt().atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                        : null)
                .build();
    }
}

