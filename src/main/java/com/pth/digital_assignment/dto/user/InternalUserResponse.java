package com.pth.digital_assignment.dto.user;

import com.pth.digital_assignment.enums.UserRole;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class InternalUserResponse {
    private UUID id;
    private String username;
    private UserRole role;
    private LocalDateTime createdAt;
}
