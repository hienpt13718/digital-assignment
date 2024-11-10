package com.pth.digital_assignment.dto.order;

import lombok.Data;

import java.util.List;

@Data
public class QueueDetails {
    private int queueNumber;
    private int currentSize;
    private List<QueueOrder> orders;
}
