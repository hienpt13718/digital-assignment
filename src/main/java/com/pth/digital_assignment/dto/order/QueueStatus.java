package com.pth.digital_assignment.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class QueueStatus {
    private Long shopId;
    private int currentQueueSize;
    private int maxQueueSize;
    private int numberOfQueues;
    private List<QueueDetails> queuesStatus;
}
