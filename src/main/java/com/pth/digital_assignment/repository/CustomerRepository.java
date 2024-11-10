package com.pth.digital_assignment.repository;

import com.pth.digital_assignment.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    boolean existsByPhone(String phone);
}
