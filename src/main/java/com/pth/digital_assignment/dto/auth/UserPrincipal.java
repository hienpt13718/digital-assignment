package com.pth.digital_assignment.dto.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

@Data
public class UserPrincipal implements Serializable {
    private String identifier;
    private List<String> authorities;
}
