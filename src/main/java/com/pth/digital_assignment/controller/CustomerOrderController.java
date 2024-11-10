package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.order.CreateOrderRequest;
import com.pth.digital_assignment.dto.order.CustomerLocation;
import com.pth.digital_assignment.dto.order.NearbyShop;
import com.pth.digital_assignment.dto.order.OrderStatusDto;
import com.pth.digital_assignment.service.OrderService;
import com.pth.digital_assignment.utils.RequestUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/customer")
@RequiredArgsConstructor
public class CustomerOrderController {
    private final OrderService orderService;

    @PostMapping("/shops/nearby")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<NearbyShop>> findNearbyShops(
            @Valid @RequestBody CustomerLocation location) {
        return ResponseEntity.ok(orderService.findNearbyShops(location));
    }

    @PostMapping("/orders")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderStatusDto> placeOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest orderRequest) {
        UUID customerId = RequestUtils.paramToUUID(userDetails.getUsername());
        return ResponseEntity.ok(orderService.placeOrder(customerId, orderRequest));
    }

    @GetMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderStatusDto> getOrderStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        UUID customerId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getOrderStatus(customerId, orderId));
    }

    @DeleteMapping("/orders/{orderId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Void> cancelOrder(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long orderId) {
        UUID customerId = UUID.fromString(userDetails.getUsername());
        orderService.cancelOrder(customerId, orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/orders/active")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderStatusDto>> getActiveOrders(
            @AuthenticationPrincipal UserDetails userDetails) {
        UUID customerId = UUID.fromString(userDetails.getUsername());
        return ResponseEntity.ok(orderService.getActiveOrders(customerId));
    }
}
