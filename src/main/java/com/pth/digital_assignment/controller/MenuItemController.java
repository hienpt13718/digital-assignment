package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.shop.CreateMenuItemRequest;
import com.pth.digital_assignment.dto.shop.MenuItemDTO;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/menu-items")
@RequiredArgsConstructor
public class MenuItemController {
    private final ShopService shopService;
    private final MenuItemService menuItemService;

    @GetMapping("/shop/{shopId}")
    public ResponseEntity<Page<MenuItemDTO>> getMenuItems(@PathVariable Long shopId,
                                                          @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(
                menuItemService.getMenuItemsByShopId(shopId, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemDTO> getMenuItemById(@PathVariable Long id) {
        return ResponseEntity.ok(
                menuItemService.getMenuItemById(id)
        );
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> addMenuItem(@Valid @RequestBody CreateMenuItemRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(menuItemService.createMenuItem(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long id,
                                                      @Valid @RequestBody MenuItemDTO menuItemDTO) {
        MenuItemDTO updatedItem = menuItemService.updateMenuItem(id, menuItemDTO);
        return ResponseEntity.ok(updatedItem);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        menuItemService.deleteMenuItem(id);
        return ResponseEntity.noContent().build();
    }
}
