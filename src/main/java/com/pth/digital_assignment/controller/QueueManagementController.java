package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.order.QueueOrder;
import com.pth.digital_assignment.dto.order.QueueStatus;
import com.pth.digital_assignment.service.QueueManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/queue")
@RequiredArgsConstructor
public class QueueManagementController {
    private final QueueManagementService queueManagementService;

    @GetMapping("/shop/{shopId}/status")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<QueueStatus> getQueueStatus(@PathVariable Long shopId) {
        return ResponseEntity.ok(queueManagementService.getQueueStatus(shopId));
    }

    @PostMapping("/shop/{shopId}/process")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<QueueOrder> processNextOrder(
            @PathVariable Long shopId,
            @RequestParam(defaultValue = "1") int queueNumber) {
        return ResponseEntity.ok(queueManagementService.processNextOrder(shopId, queueNumber));
    }

    @PutMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAnyAuthority('OPERATOR')")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String status) {
        queueManagementService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}
