package com.mss301.kraft.cart_service.service;

import com.mss301.kraft.cart_service.dto.CartResponse;
import com.mss301.kraft.cart_service.dto.CartItemResponse;
import com.mss301.kraft.cart_service.dto.AddItemRequest;
import com.mss301.kraft.cart_service.dto.UpdateItemRequest;
import com.mss301.kraft.cart_service.entity.Cart;
import com.mss301.kraft.cart_service.entity.CartItem;
import com.mss301.kraft.cart_service.repository.CartItemRepository;
import com.mss301.kraft.cart_service.repository.CartRepository;
import com.mss301.kraft.product_service.entity.ProductVariant;
import com.mss301.kraft.product_service.repository.ProductVariantRepository;
import com.mss301.kraft.user_service.entity.User;
import com.mss301.kraft.user_service.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductVariantRepository productVariantRepository;
    private final UserRepository userRepository;

    public CartService(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductVariantRepository productVariantRepository,
            UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productVariantRepository = productVariantRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse create() {
        // Idempotent: return existing cart for current user if present, else create new
        User currentUser = resolveCurrentUser();
        List<Cart> existingForUser = cartRepository.findByUser(currentUser);
        if (existingForUser != null && !existingForUser.isEmpty()) {
            // Return the most recent cart
            return toResponse(existingForUser.get(0));
        }

        Cart cart = new Cart();
        cart.setUser(currentUser);
        cart.setTotal(BigDecimal.ZERO);
        cart = cartRepository.save(cart);
        return toResponse(cart);
    }

    private User resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) {
            throw new IllegalStateException("No authenticated user");
        }
        String username = auth.getName();
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));
    }

    @Transactional(readOnly = true)
    public CartResponse get(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        assertOwnership(cart);
        return toResponse(cart);
    }

    @Transactional
    public CartResponse addItem(UUID cartId, AddItemRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        assertOwnership(cart);
        ProductVariant variant = productVariantRepository.findById(request.getVariantId())
                .orElseThrow(() -> new IllegalArgumentException("Variant not found"));
        int qty = Math.max(1, request.getQty() == null ? 1 : request.getQty());
        int stock = variant.getStock() == null ? Integer.MAX_VALUE : variant.getStock();
        int addQty = Math.min(qty, stock);
        if (addQty <= 0) {
            return toResponse(cart);
        }

        CartItem item = cartItemRepository.findByCartAndProductVariant(cart, variant)
                .orElse(null);
        if (item == null) {
            item = new CartItem();
            item.setCart(cart);
            item.setProductVariant(variant);
            item.setQty(addQty);
            item.setPrice(variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice());
        } else {
            int next = item.getQty() + addQty;
            item.setQty(Math.min(next, stock));
        }
        cartItemRepository.save(item);
        recalcTotal(cart);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(UUID cartId, UUID itemId, UpdateItemRequest request) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        assertOwnership(cart);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item not in cart");
        }
        int qty = request.getQty() == null ? item.getQty() : request.getQty();
        if (qty <= 0) {
            cartItemRepository.delete(item);
            try {
                if (cart.getItems() != null) {
                    cart.getItems().removeIf(ci -> ci.getId().equals(item.getId()));
                }
            } catch (Exception ignored) {
            }
        } else {
            int stock = item.getProductVariant().getStock() == null ? Integer.MAX_VALUE
                    : item.getProductVariant().getStock();
            item.setQty(Math.min(qty, stock));
            cartItemRepository.save(item);
        }
        recalcTotal(cart);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(UUID cartId, UUID itemId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        assertOwnership(cart);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("CartItem not found"));
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new IllegalArgumentException("Item not in cart");
        }
        cartItemRepository.delete(item);
        try {
            if (cart.getItems() != null) {
                cart.getItems().removeIf(ci -> ci.getId().equals(item.getId()));
            }
        } catch (Exception ignored) {
        }
        recalcTotal(cart);
        return toResponse(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse clear(UUID cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new IllegalArgumentException("Cart not found"));
        assertOwnership(cart);
        for (CartItem it : cartItemRepository.findByCart(cart)) {
            cartItemRepository.delete(it);
        }
        recalcTotal(cart);
        try {
            if (cart.getItems() != null) {
                cart.getItems().clear();
            }
        } catch (Exception ignored) {
        }
        cartRepository.save(cart);
        return toResponse(cart);
    }

    private void assertOwnership(Cart cart) {
        User currentUser = resolveCurrentUser();
        if (cart.getUser() == null || currentUser == null || cart.getUser().getId() == null) {
            throw new IllegalArgumentException("Cart has no owner");
        }
        if (!cart.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("Access to another user's cart is not allowed");
        }
    }

    private void recalcTotal(Cart cart) {
        BigDecimal total = cartItemRepository.findByCart(cart).stream()
                .map(it -> it.getPrice().multiply(BigDecimal.valueOf(it.getQty())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotal(total);
    }

    private CartResponse toResponse(Cart cart) {
        // Always fetch a fresh list to avoid stale proxies after delete operations
        List<CartItem> items = cartItemRepository.findByCart(cart);
        List<CartItemResponse> itemResponses = items.stream().map(it -> CartItemResponse.builder()
                .id(it.getId())
                .variantId(it.getProductVariant().getId())
                .sku(it.getProductVariant().getSku())
                .color(it.getProductVariant().getColor())
                .size(it.getProductVariant().getSize())
                .imageUrl(it.getProductVariant().getImageUrl())
                .qty(it.getQty())
                .unitPrice(it.getPrice())
                .lineTotal(it.getPrice().multiply(BigDecimal.valueOf(it.getQty())))
                .build()).toList();
        return CartResponse.builder()
                .id(cart.getId())
                .items(itemResponses)
                .total(cart.getTotal())
                .build();
    }
}
