package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.shop.CreateShopRequest;
import com.pth.digital_assignment.dto.shop.ShopResponse;
import com.pth.digital_assignment.dto.shop.UpdateShopRequest;
import com.pth.digital_assignment.exception.ResourceNotFoundException;
import com.pth.digital_assignment.mapper.ShopMapper;
import com.pth.digital_assignment.model.Shop;
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
public class ShopServiceImpl implements ShopService {
    private final ShopMapper shopMapper;
    private final ShopRepository shopRepository;


    @Override
    @Transactional
    public ShopResponse createShop(CreateShopRequest request) {
        Shop shop = shopMapper.toEntity(request);
        shop = shopRepository.save(shop);

        return shopMapper.toResponse(shop);
    }

    @Override
    @Transactional
    public ShopResponse updateShop(Long id, UpdateShopRequest request) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(id));
        shopMapper.updateShopFromRequest(request, shop);
        shop = shopRepository.save(shop);

        return shopMapper.toResponse(shop);
    }

    @Override
    public ShopResponse getShop(Long id) {
        Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(id));
        return shopMapper.toResponse(shop);
    }

    @Override
    public Page<ShopResponse> getAllShops(Pageable pageable) {
        return shopRepository.findAll(pageable)
                .map(shopMapper::toResponse);
    }

    @Override
    public Page<ShopResponse> findNearbyShops(Double latitude, Double longitude, Double radiusInKm, Pageable pageable) {
        return shopRepository.findNearbyShops(latitude, longitude, radiusInKm, pageable)
                .map(shopMapper::toResponse);
    }
}
