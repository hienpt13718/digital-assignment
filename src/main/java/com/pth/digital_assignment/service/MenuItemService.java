package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.shop.CreateMenuItemRequest;
import com.pth.digital_assignment.dto.shop.MenuItemDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuItemService {
    MenuItemDTO createMenuItem(Long shopId, CreateMenuItemRequest request);
    Page<MenuItemDTO> getMenuItemsByShopId(Long shopId, Pageable pageable);
    MenuItemDTO getMenuItemById(Long id);
    void deleteMenuItem(Long id);
    MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO);
}
