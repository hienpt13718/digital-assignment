package com.pth.digital_assignment.mapper;

import com.pth.digital_assignment.dto.shop.CreateShopRequest;
import com.pth.digital_assignment.dto.shop.ShopResponse;
import com.pth.digital_assignment.dto.shop.UpdateShopRequest;
import com.pth.digital_assignment.model.Shop;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface ShopMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    Shop toEntity(CreateShopRequest request);

    ShopResponse toResponse(Shop shop);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateShopFromRequest(UpdateShopRequest request, @MappingTarget Shop shop);
}
