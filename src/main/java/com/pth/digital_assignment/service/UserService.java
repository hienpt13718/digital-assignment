package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.user.CreateInternalUserRequest;
import com.pth.digital_assignment.dto.user.InternalUserResponse;
import com.pth.digital_assignment.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    InternalUserResponse createInternalUser(CreateInternalUserRequest request);
}
