package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.order.CreateOrderRequest;
import com.pth.digital_assignment.dto.order.CustomerLocation;
import com.pth.digital_assignment.dto.order.NearbyShop;
import com.pth.digital_assignment.dto.order.OrderStatusDto;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    List<NearbyShop> findNearbyShops(CustomerLocation location);
    OrderStatusDto placeOrder(UUID customerId, CreateOrderRequest orderRequest);
    OrderStatusDto getOrderStatus(UUID customerId, Long orderId);
    void cancelOrder(UUID customerId, Long orderId);
    List<OrderStatusDto> getActiveOrders(UUID customerId);
}
