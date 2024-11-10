package com.pth.digital_assignment.dto.auth;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserPrincipal implements Serializable {
    private String identifier;
    private List<String> authorities;
}
