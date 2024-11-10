package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.order.QueueOrder;
import com.pth.digital_assignment.dto.order.QueueStatus;

public interface QueueManagementService {
    QueueStatus getQueueStatus(Long shopId);
    QueueOrder processNextOrder(Long shopId, int queueNumber);
    void updateOrderStatus(Long orderId, String status);
    boolean isQueueFull(Long shopId, int queueNumber);
    int getNextQueuePosition(Long shopId, int queueNumber);
    Integer getAvailableQueue(Long shopId);
}
