package com.pth.digital_assignment.repository;

import com.pth.digital_assignment.enums.OrderStatus;
import com.pth.digital_assignment.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, Long> {

    long countByShopIdAndStatusIn(Long shopId, List<OrderStatus> statuses);

    List<Order> findByShopIdAndQueuePositionGreaterThan(Long shopId, int position);

    List<Order> findByShopIdAndStatusInOrderByQueuePosition(Long shopId, List<OrderStatus> statuses);

    Optional<Order> findFirstByShopIdAndStatusOrderByQueuePosition(Long shopId, OrderStatus status);

    @Modifying
    @Query("UPDATE Order o SET o.queuePosition = o.queuePosition - 1 " +
           "WHERE o.shop.id = ?2 AND o.queueNumber = ?3 AND o.queuePosition > ?1 AND o.status = 'IN_QUEUE'")
    void decrementQueuePositionsAfter(int position, Long shopId, int queueNumber);

    Optional<Order> findFirstByShopIdAndQueueNumberAndStatusOrderByQueuePosition(
            Long shopId, int queueNumber, OrderStatus status);

    List<Order> findByShopIdAndQueueNumberAndStatusOrderByQueuePosition(
            Long shopId, int queueNumber, OrderStatus status);

    int countByShopIdAndQueueNumberAndStatus(Long shopId, int queueNumber, OrderStatus orderStatus);

    @Query("SELECT MAX(o.queuePosition) FROM Order o WHERE o.shop.id = ?1 AND o.queueNumber = ?2")
    Integer findMaxQueuePositionByShopIdAndQueueNumber(Long shopId, int queueNumber);

    int countByShopIdAndStatus(Long id, OrderStatus orderStatus);

    Optional<Order> findByIdAndCustomerId(Long orderId, UUID customerId);

    List<Order> findByCustomerIdAndStatusIn(UUID customerId, List<OrderStatus> statuses);
}
