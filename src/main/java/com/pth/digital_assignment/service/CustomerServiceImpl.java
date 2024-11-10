package com.pth.digital_assignment.service;

import com.pth.digital_assignment.dto.customer.CustomerProfileResponse;
import com.pth.digital_assignment.dto.customer.CustomerRegistrationRequest;
import com.pth.digital_assignment.dto.customer.UpdateCustomerProfileRequest;
import com.pth.digital_assignment.enums.UserRole;
import com.pth.digital_assignment.enums.UserStatus;
import com.pth.digital_assignment.exception.ResourceDuplicatedException;
import com.pth.digital_assignment.exception.ResourceNotFoundException;
import com.pth.digital_assignment.mapper.CustomerMapper;
import com.pth.digital_assignment.model.Customer;
import com.pth.digital_assignment.model.User;
import com.pth.digital_assignment.repository.CustomerRepository;
import com.pth.digital_assignment.repository.UserRepository;
import com.pth.digital_assignment.utils.RequestUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CustomerMapper customerMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CustomerProfileResponse registerCustomer(CustomerRegistrationRequest request) {
        // Check if phone is already registered
        if (userRepository.existsByUsername(request.getPhone())) {
            throw ResourceDuplicatedException.customerPhoneDuplicated();
        }

        // Create user
        User user = User.builder()
                .id(UUID.randomUUID())
                .username(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.CUSTOMER)
                .status(UserStatus.ACTIVE)
                .build();
        user = userRepository.save(user);

        // Create customer
        Customer customer = customerMapper.toEntity(request, user);
        customer = customerRepository.save(customer);

        return customerMapper.toProfileResponse(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public CustomerProfileResponse getCustomerProfile(String customerId) {
        UUID uuidCustomerId = RequestUtils.paramToUUID(customerId);
        Customer customer = findCustomerById(uuidCustomerId);

        return customerMapper.toProfileResponse(customer);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateCustomerProfile(String customerId,
                                                         UpdateCustomerProfileRequest request) {
        UUID uuidCustomerId = RequestUtils.paramToUUID(customerId);
        Customer customer = findCustomerById(uuidCustomerId);

        // Check if new phone number is already taken
        if (request.getPhone() != null &&
            !request.getPhone().equals(customer.getPhone()) &&
            customerRepository.existsByPhone(request.getPhone())) {
            throw ResourceDuplicatedException.customerPhoneDuplicated();
        }

        customerMapper.updateCustomerFromRequest(request, customer);
        customer = customerRepository.save(customer);

        return customerMapper.toProfileResponse(customer);
    }

    @Override
    @Transactional
    public void incrementLoyaltyScore(UUID customerId) {
        Customer customer = findCustomerById(customerId);
        customer.setLoyaltyScore(customer.getLoyaltyScore() + 1);
        customerRepository.save(customer);
    }

    private Customer findCustomerById(UUID id) {
        return customerRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.customerNotFound(id));
    }
}
