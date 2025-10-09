package com.mss301.kraft.cart_service.controller;

import com.mss301.kraft.cart_service.dto.CartResponse;
import com.mss301.kraft.cart_service.dto.AddItemRequest;
import com.mss301.kraft.cart_service.dto.UpdateItemRequest;
import com.mss301.kraft.cart_service.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Create a cart for current user (or guest user placeholder)
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartResponse> create() {
        return ResponseEntity.ok(cartService.create());
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartResponse> get(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartService.get(cartId));
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable UUID cartId, @RequestBody AddItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(cartId, request));
    }

    @PutMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateItem(@PathVariable UUID cartId, @PathVariable UUID itemId,
            @RequestBody UpdateItemRequest request) {
        return ResponseEntity.ok(cartService.updateItem(cartId, itemId, request));
    }

    @DeleteMapping("/{cartId}/items/{itemId}")
    public ResponseEntity<CartResponse> removeItem(@PathVariable UUID cartId, @PathVariable UUID itemId) {
        return ResponseEntity.ok(cartService.removeItem(cartId, itemId));
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<CartResponse> clear(@PathVariable UUID cartId) {
        return ResponseEntity.ok(cartService.clear(cartId));
    }
}
