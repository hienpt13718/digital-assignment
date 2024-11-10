package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.shop.CreateMenuItemRequest;
import com.pth.digital_assignment.dto.shop.MenuItemDTO;
import com.pth.digital_assignment.exception.ResourceNotFoundException;
import com.pth.digital_assignment.mapper.MenuItemMapper;
import com.pth.digital_assignment.model.MenuItem;
import com.pth.digital_assignment.model.Shop;
import com.pth.digital_assignment.repository.MenuItemRepository;
import com.pth.digital_assignment.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    private final MenuItemMapper menuItemMapper;


    @Override
    @Transactional
    public MenuItemDTO createMenuItem(Long shopId, CreateMenuItemRequest request) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(shopId));

        MenuItem menuItem = menuItemMapper.toEntity(request);
        menuItem.setShop(shop);

        MenuItem savedMenuItem = menuItemRepository.save(menuItem);
        return menuItemMapper.toDTO(savedMenuItem);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MenuItemDTO> getMenuItemsByShopId(Long shopId, Pageable pageable) {
        return menuItemRepository.findByShopId(shopId, pageable)
                .map(menuItemMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public MenuItemDTO getMenuItemById(Long id) {
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item not found with id: " + id));
        return menuItemMapper.toDTO(menuItem);
    }

    @Override
    @Transactional
    public void deleteMenuItem(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new ResourceNotFoundException("Menu item not found with id: " + id);
        }
        menuItemRepository.deleteById(id);
    }

    @Override
    @Transactional
    public MenuItemDTO updateMenuItem(Long id, MenuItemDTO menuItemDTO) {
        if (!menuItemRepository.existsById(id)) {
            throw ResourceNotFoundException.menuItemNotFound(id);
        }

        Shop shop = shopRepository.findById(menuItemDTO.getShopId())
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(menuItemDTO.getShopId()));

        MenuItem updatedMenuItem = menuItemMapper.toEntity(menuItemDTO);
        updatedMenuItem.setId(id);
        updatedMenuItem.setShop(shop);

        MenuItem savedMenuItem = menuItemRepository.save(updatedMenuItem);
        return menuItemMapper.toDTO(savedMenuItem);
    }
}
