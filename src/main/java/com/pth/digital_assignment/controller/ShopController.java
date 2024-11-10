package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.shop.CreateShopRequest;
import com.pth.digital_assignment.dto.shop.ShopResponse;
import com.pth.digital_assignment.dto.shop.UpdateShopRequest;
import com.pth.digital_assignment.service.MenuItemService;
import com.pth.digital_assignment.service.ShopService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/shops")
@RequiredArgsConstructor
public class ShopController {
    private final ShopService shopService;
    private final MenuItemService menuItemService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ShopResponse> createShop(@Valid @RequestBody CreateShopRequest request) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(shopService.createShop(request));
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<ShopResponse> updateShop(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateShopRequest request) {
        return ResponseEntity.ok(shopService.updateShop(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShopResponse> getShop(@PathVariable Long id) {
        return ResponseEntity.ok(shopService.getShop(id));
    }

    @GetMapping
    public ResponseEntity<Page<ShopResponse>> getAllShops(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(shopService.getAllShops(pageable));
    }
}
