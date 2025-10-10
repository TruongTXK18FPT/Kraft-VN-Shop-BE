package com.mss301.kraft.payment_service.controller;

import com.mss301.kraft.payment_service.dto.BankAccountRequest;
import com.mss301.kraft.payment_service.dto.BankAccountResponse;
import com.mss301.kraft.payment_service.service.BankAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/bank-accounts")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class BankAccountController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public ResponseEntity<List<BankAccountResponse>> getAllBankAccounts() {
        List<BankAccountResponse> bankAccounts = bankAccountService.getAllBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/active")
    public ResponseEntity<List<BankAccountResponse>> getActiveBankAccounts() {
        List<BankAccountResponse> bankAccounts = bankAccountService.getActiveBankAccounts();
        return ResponseEntity.ok(bankAccounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BankAccountResponse> getBankAccountById(@PathVariable UUID id) {
        BankAccountResponse bankAccount = bankAccountService.getBankAccountById(id);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping
    public ResponseEntity<BankAccountResponse> createBankAccount(@Valid @RequestBody BankAccountRequest request) {
        BankAccountResponse bankAccount = bankAccountService.createBankAccount(request);
        return ResponseEntity.ok(bankAccount);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BankAccountResponse> updateBankAccount(
            @PathVariable UUID id,
            @Valid @RequestBody BankAccountRequest request) {
        BankAccountResponse bankAccount = bankAccountService.updateBankAccount(id, request);
        return ResponseEntity.ok(bankAccount);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable UUID id) {
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<BankAccountResponse> toggleBankAccountStatus(@PathVariable UUID id) {
        BankAccountResponse bankAccount = bankAccountService.toggleBankAccountStatus(id);
        return ResponseEntity.ok(bankAccount);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<ImageUploadResponse> uploadBankAccountImage(@RequestParam("file") MultipartFile file) {
        // TODO: Implement image upload logic
        // For now, return a mock response
        String imageUrl = "/uploads/bank-accounts/" + UUID.randomUUID() + ".jpg";
        return ResponseEntity.ok(new ImageUploadResponse(imageUrl));
    }

    // Inner class for image upload response
    public static class ImageUploadResponse {
        private String imageUrl;

        public ImageUploadResponse(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }
    }
}

