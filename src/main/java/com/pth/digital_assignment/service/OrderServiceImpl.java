package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.order.CreateOrderRequest;
import com.pth.digital_assignment.dto.order.CustomerLocation;
import com.pth.digital_assignment.dto.order.NearbyShop;
import com.pth.digital_assignment.dto.order.OrderItemDto;
import com.pth.digital_assignment.dto.order.OrderItemRequest;
import com.pth.digital_assignment.dto.order.OrderStatusDto;
import com.pth.digital_assignment.dto.order.ShopBasicInfo;
import com.pth.digital_assignment.exception.BadRequestException;
import com.pth.digital_assignment.exception.ResourceNotFoundException;
import com.pth.digital_assignment.model.Customer;
import com.pth.digital_assignment.model.MenuItem;
import com.pth.digital_assignment.model.Order;
import com.pth.digital_assignment.model.OrderItem;
import com.pth.digital_assignment.model.Shop;
import com.pth.digital_assignment.repository.CustomerRepository;
import com.pth.digital_assignment.repository.MenuItemRepository;
import com.pth.digital_assignment.repository.OrderItemRepository;
import com.pth.digital_assignment.repository.OrderRepository;
import com.pth.digital_assignment.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.pth.digital_assignment.enums.OrderStatus.CANCELLED;
import static com.pth.digital_assignment.enums.OrderStatus.IN_QUEUE;
import static com.pth.digital_assignment.enums.OrderStatus.PENDING;
import static com.pth.digital_assignment.enums.OrderStatus.PROCESSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final CustomerRepository customerRepository;
    private final QueueManagementService queueManagementService;

    private static final int AVERAGE_PREPARATION_TIME_MINUTES = 5;// TODO: should configurable

    @Override
    @Transactional(readOnly = true)
    public List<NearbyShop> findNearbyShops(CustomerLocation location) {
        List<Shop> allShops = shopRepository.findAll();
        LocalTime currentTime = LocalTime.now();

        return allShops.stream()
                .map(shop -> {
                    NearbyShop dto = new NearbyShop();
                    dto.setId(shop.getId());
                    dto.setName(shop.getName());
                    dto.setAddress(shop.getAddress());
                    dto.setContactNumber(shop.getPhone());
                    dto.setLatitude(shop.getLatitude());
                    dto.setLongitude(shop.getLongitude());

                    // Calculate distance
                    double distance = calculateDistance(
                            location.getLatitude(), location.getLongitude(),
                            shop.getLatitude(), shop.getLongitude()
                    );
                    dto.setDistanceInKm(distance);

                    // Check if shop is open
                    dto.setOpeningTime(shop.getOpeningTime());
                    dto.setClosingTime(shop.getClosingTime());
                    dto.setOpen(isShopOpen(shop, currentTime));

                    // Get queue information
                    int queueSize = orderRepository.countByShopIdAndStatus(shop.getId(), IN_QUEUE);
                    dto.setCurrentQueueSize(queueSize);
                    dto.setEstimatedWaitTimeMinutes(calculateEstimatedWaitTime(queueSize));

                    return dto;
                })
                .filter(shop -> shop.getDistanceInKm() <= location.getRadiusInKm())
                .sorted(Comparator.comparingDouble(NearbyShop::getDistanceInKm))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public OrderStatusDto placeOrder(UUID customerId, CreateOrderRequest orderRequest) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> ResourceNotFoundException.customerNotFound(customerId));

        Shop shop = shopRepository.findById(orderRequest.getShopId())
                .orElseThrow(() ->ResourceNotFoundException.shopNotFound(orderRequest.getShopId()));

        // Validate shop is open
        if (!isShopOpen(shop, LocalTime.now())) {
            throw BadRequestException.shopIsClosed();
        }

        Integer availableQueue = queueManagementService.getAvailableQueue(shop.getId());

        // Validate queue is not full
        if (availableQueue == null) {
            throw BadRequestException.queueIsFull();
        }

        Integer queuePosition = queueManagementService.getNextQueuePosition(shop.getId(), availableQueue);

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setShop(shop);
        order.setStatus(IN_QUEUE);
        order.setCreatedAt(LocalDateTime.now());
        order.setQueueNumber(availableQueue);
        order.setQueuePosition(queuePosition);

        // Add order items
        List<OrderItem> orderItems = new ArrayList<>();
        double totalAmount = 0.0;

        for (OrderItemRequest itemRequest : orderRequest.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> ResourceNotFoundException.menuItemNotFound(itemRequest.getMenuItemId()));

            if (!menuItem.getIsAvailable()) {
                throw BadRequestException.menuItemIsNotAvailable(menuItem.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemRequest.getQuantity());
            orderItem.setUnitPrice(menuItem.getPrice());
            orderItems.add(orderItem);

            totalAmount += menuItem.getPrice().doubleValue() * itemRequest.getQuantity();
        }

        Order savedOrder = orderRepository.save(order);

        orderItems.forEach(item -> item.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        return createOrderStatusDto(order, orderItems, shop);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderStatusDto getOrderStatus(UUID customerId, Long orderId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);

        return createOrderStatusDto(order, orderItems, order.getShop());
    }

    @Override
    @Transactional
    public void cancelOrder(UUID customerId, Long orderId) {
        Order order = orderRepository.findByIdAndCustomerId(orderId, customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (!Arrays.asList(IN_QUEUE, PENDING).contains(order.getStatus())) {
            throw BadRequestException.invalidOrderStatusForCancelling();
        }

        order.setStatus(CANCELLED);
        orderRepository.save(order);

        // Update queue positions for remaining orders
        if (order.getQueuePosition() != null) {
            orderRepository.decrementQueuePositionsAfter(
                    order.getQueuePosition(),
                    order.getShop().getId(),
                    order.getQueueNumber());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderStatusDto> getActiveOrders(UUID customerId) {
        List<Order> activeOrders = orderRepository.findByCustomerIdAndStatusIn(
                customerId,
                Arrays.asList(PENDING, IN_QUEUE, PROCESSING)
        );

        return activeOrders.stream()
                .map(order -> {
                    List<OrderItem> items = orderItemRepository.findByOrderId(order.getId());
                    return createOrderStatusDto(order, items, order.getShop());
                })
                .collect(Collectors.toList());
    }

    private boolean isShopOpen(Shop shop, LocalTime currentTime) {
        return !currentTime.isBefore(shop.getOpeningTime()) &&
               !currentTime.isAfter(shop.getClosingTime());
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula implementation
        final int R = 6371; // Earth's radius in kilometers

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);

        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                   + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                     * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return R * c;
    }

    private int calculateEstimatedWaitTime(int queuePosition) {
        return queuePosition * AVERAGE_PREPARATION_TIME_MINUTES;
    }

    private OrderStatusDto createOrderStatusDto(Order order, List<OrderItem> items, Shop shop) {
        OrderStatusDto dto = new OrderStatusDto();
        dto.setOrderId(order.getId());
        dto.setStatus(order.getStatus());
        dto.setQueuePosition(order.getQueuePosition());

        ShopBasicInfo shopDto = new ShopBasicInfo();
        shopDto.setId(shop.getId());
        shopDto.setName(shop.getName());
        shopDto.setAddress(shop.getAddress());
        shopDto.setContactNumber(shop.getPhone());
        dto.setShop(shopDto);

        List<OrderItemDto> itemDtos = items.stream()
                .map(OrderServiceImpl::toOrderItemDto)
                .collect(Collectors.toList());
        dto.setItems(itemDtos);

        BigDecimal totalAmount = items.stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        dto.setTotalAmount(totalAmount);

        // Calculate estimated wait time based on queue position
        dto.setEstimatedWaitTimeMinutes(calculateEstimatedWaitTime(order.getQueuePosition()));

        return dto;
    }

    private static OrderItemDto toOrderItemDto(OrderItem item) {
        OrderItemDto itemDto = new OrderItemDto();
        itemDto.setMenuItemId(item.getMenuItem().getId());
        itemDto.setItemName(item.getMenuItem().getName());
        itemDto.setQuantity(item.getQuantity());
        itemDto.setUnitPrice(item.getUnitPrice().doubleValue());
        return itemDto;
    }
}
