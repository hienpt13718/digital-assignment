package com.pth.digital_assignment.controller;

import com.pth.digital_assignment.dto.user.CreateInternalUserRequest;
import com.pth.digital_assignment.dto.user.InternalUserResponse;
import com.pth.digital_assignment.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InternalUserResponse> registerUser(
            @Valid @RequestBody CreateInternalUserRequest request) {
        InternalUserResponse user = userService.createInternalUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(user);
    }
}
