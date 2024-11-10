package com.pth.digital_assignment.mapper;

import com.pth.digital_assignment.dto.shop.CreateMenuItemRequest;
import com.pth.digital_assignment.dto.shop.MenuItemDTO;
import com.pth.digital_assignment.model.MenuItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MenuItemMapper {
    @Mapping(target = "shopId", source = "shop.id")
    MenuItemDTO toDTO(MenuItem menuItem);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "shop.id", source = "shopId")
    MenuItem toEntity(CreateMenuItemRequest request);

    @Mapping(target = "shop.id", source = "shopId")
    MenuItem toEntity(MenuItemDTO menuItemDTO);
}
