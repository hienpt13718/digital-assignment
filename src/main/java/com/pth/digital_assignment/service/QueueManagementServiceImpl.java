package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.order.CustomerInfo;
import com.pth.digital_assignment.dto.order.OrderItemDto;
import com.pth.digital_assignment.dto.order.QueueDetails;
import com.pth.digital_assignment.dto.order.QueueOrder;
import com.pth.digital_assignment.dto.order.QueueStatus;
import com.pth.digital_assignment.enums.OrderStatus;
import com.pth.digital_assignment.exception.BadRequestException;
import com.pth.digital_assignment.exception.ResourceNotFoundException;
import com.pth.digital_assignment.model.Customer;
import com.pth.digital_assignment.model.Order;
import com.pth.digital_assignment.model.Shop;
import com.pth.digital_assignment.repository.CustomerRepository;
import com.pth.digital_assignment.repository.OrderItemRepository;
import com.pth.digital_assignment.repository.OrderRepository;
import com.pth.digital_assignment.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.pth.digital_assignment.enums.OrderStatus.COMPLETED;
import static com.pth.digital_assignment.enums.OrderStatus.IN_QUEUE;
import static com.pth.digital_assignment.enums.OrderStatus.PROCESSING;

@Slf4j
@Service
@RequiredArgsConstructor
public class QueueManagementServiceImpl implements QueueManagementService {
    private final ShopRepository shopRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;

    @Override
    @Transactional(readOnly = true)
    public QueueStatus getQueueStatus(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(shopId));

        List<QueueDetails> queuesStatus = new ArrayList<>();
        for (int i = 1; i <= shop.getNumberOfQueues(); i++) {
            List<Order> queuedOrders = orderRepository
                    .findByShopIdAndQueueNumberAndStatusOrderByQueuePosition(shopId, i, IN_QUEUE);

            QueueDetails queueDetails = new QueueDetails();
            queueDetails.setQueueNumber(i);
            queueDetails.setCurrentSize(queuedOrders.size());
            queueDetails.setOrders(queuedOrders.stream()
                    .map(this::convertToQueueOrderDTO)
                    .collect(Collectors.toList()));
            queuesStatus.add(queueDetails);
        }

        QueueStatus status = new QueueStatus();
        status.setShopId(shopId);
        status.setMaxQueueSize(shop.getMaxQueueSize());
        status.setNumberOfQueues(shop.getNumberOfQueues());
        status.setQueuesStatus(queuesStatus);

        return status;
    }

    // TODO: we have AVERAGE_PREPARATION_TIME_MINUTES = 5 minutes. So that we can have a job for check and change PROCESS to COMPLETED.
    @Override
    @Transactional
    public QueueOrder processNextOrder(Long shopId, int queueNumber) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(shopId));

        if (queueNumber <= 0 || queueNumber > shop.getNumberOfQueues()) {
            throw new BadRequestException("INVALID_QUEUE_NUMBER", "Invalid queue number");
        }

        Order nextOrder = orderRepository.findFirstByShopIdAndQueueNumberAndStatusOrderByQueuePosition(
                        shopId, queueNumber, IN_QUEUE)
                .orElseThrow(() -> ResourceNotFoundException.noOrderInQueue(queueNumber));

        nextOrder.setStatus(PROCESSING);
        Order updatedOrder = orderRepository.save(nextOrder);

        // Update queue positions for remaining orders in the same queue
        orderRepository.decrementQueuePositionsAfter(nextOrder.getQueuePosition(), shopId, queueNumber);

        return convertToQueueOrderDTO(updatedOrder);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> ResourceNotFoundException.orderNotFound(orderId));

        OrderStatus orderStatus = OrderStatus.of(status);
        order.setStatus(orderStatus);

        if (COMPLETED.equals(orderStatus)) {
            // Update customer loyalty score
            Customer customer = order.getCustomer();
            customer.setLoyaltyScore(customer.getLoyaltyScore() + 1);
            customerRepository.save(customer);
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isQueueFull(Long shopId, int queueNumber) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(shopId));

        if (queueNumber <= 0 || queueNumber > shop.getNumberOfQueues()) {
            throw BadRequestException.invalidQueueNum();
        }

        return isQueueFull(shopId, queueNumber, shop.getMaxQueueSize());
    }

    @Override
    @Transactional(readOnly = true)
    public int getNextQueuePosition(Long shopId, int queueNumber) {
        Integer maxPosition = orderRepository.findMaxQueuePositionByShopIdAndQueueNumber(shopId, queueNumber);
        return (maxPosition == null) ? 1 : maxPosition + 1;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableQueue(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> ResourceNotFoundException.shopNotFound(shopId));
        for (int i = 1; i <= shop.getNumberOfQueues(); i++) {
            if (!isQueueFull(shopId, i, shop.getMaxQueueSize())) {
                return i;
            }
        }

        return null;
    }

    private QueueOrder convertToQueueOrderDTO(Order order) {
        QueueOrder dto = new QueueOrder();
        dto.setOrderId(order.getId());
        dto.setQueuePosition(order.getQueuePosition());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());

        CustomerInfo customerInfo = new CustomerInfo();
        Customer customer = order.getCustomer();
        customerInfo.setCustomerId(customer.getId());
        customerInfo.setName(customer.getName());
        customerInfo.setPhone(customer.getPhone());
        customerInfo.setLoyaltyScore(customer.getLoyaltyScore());
        dto.setCustomer(customerInfo);

        // TODO: performance
        List<OrderItemDto> items = orderItemRepository.findByOrderId(order.getId())
                .stream()
                .map(item -> {
                    OrderItemDto itemDTO = new OrderItemDto();
                    itemDTO.setMenuItemId(item.getMenuItem().getId());
                    itemDTO.setItemName(item.getMenuItem().getName());
                    itemDTO.setQuantity(item.getQuantity());
                    itemDTO.setUnitPrice(item.getUnitPrice().doubleValue());
                    return itemDTO;
                })
                .collect(Collectors.toList());
        dto.setItems(items);

        Double totalAmount = items.stream()
                .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
                .sum();
        dto.setTotalAmount(totalAmount);

        return dto;
    }

    private boolean isQueueFull(Long shopId, int queueNumber, int maxQueueSize) {
        int currentQueueSize = orderRepository.countByShopIdAndQueueNumberAndStatus(
                shopId, queueNumber, IN_QUEUE);
        return currentQueueSize >= maxQueueSize;
    }
}
