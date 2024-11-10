package com.pth.digital_assignment.service;

import com.pth.digital_assignment.constant.Constants;
import com.pth.digital_assignment.dto.user.CreateInternalUserRequest;
import com.pth.digital_assignment.dto.user.InternalUserResponse;
import com.pth.digital_assignment.enums.UserRole;
import com.pth.digital_assignment.enums.UserStatus;
import com.pth.digital_assignment.exception.AuthException;
import com.pth.digital_assignment.exception.BadRequestException;
import com.pth.digital_assignment.exception.ResourceDuplicatedException;
import com.pth.digital_assignment.mapper.UserMapper;
import com.pth.digital_assignment.model.User;
import com.pth.digital_assignment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final ApplicationContext applicationContext;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (!UserStatus.ACTIVE.equals(user.getStatus())) {
            throw new AuthException("UNACTIVATED","Unactivated user");
        }

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getId().toString())
                .roles(user.getRole().name())
                .password(user.getPassword())
                .build();
        return userDetails;
    }

    @Override
    @Transactional
    public InternalUserResponse createInternalUser(CreateInternalUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw ResourceDuplicatedException.usernameDuplicated();
        }

        var patternPassword = Pattern.compile(Constants.PASSWORD_REGEX, Pattern.CASE_INSENSITIVE);
        if (!patternPassword.matcher(request.getPassword()).matches()) {
            throw BadRequestException.passwordIsNotSyntacticallyCorrect();
        }

        if (UserRole.CUSTOMER.equals(request.getRole())) {
            throw BadRequestException.onlySupport(List.of(UserRole.ADMIN, UserRole.OPERATOR));
        }
        PasswordEncoder passwordEncoder = applicationContext.getBean(PasswordEncoder.class);

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(request.getRole());
        user.setStatus(UserStatus.ACTIVE);

        user = userRepository.save(user);

        return userMapper.toInternalUserResponse(user);
    }
}
