package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.shop.CreateShopRequest;
import com.pth.digital_assignment.dto.shop.ShopResponse;
import com.pth.digital_assignment.dto.shop.UpdateShopRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShopService {
    ShopResponse createShop(CreateShopRequest request);

    ShopResponse updateShop(Long id, UpdateShopRequest request);

    ShopResponse getShop(Long id);

    Page<ShopResponse> getAllShops(Pageable pageable);

    Page<ShopResponse> findNearbyShops(Double latitude, Double longitude, Double radiusInKm, Pageable pageable);
}
