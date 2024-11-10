package com.pth.digital_assignment.mapper;

import com.pth.digital_assignment.dto.customer.CustomerProfileResponse;
import com.pth.digital_assignment.dto.customer.CustomerRegistrationRequest;
import com.pth.digital_assignment.dto.customer.UpdateCustomerProfileRequest;
import com.pth.digital_assignment.model.Customer;
import com.pth.digital_assignment.model.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring"/*, uses = { UserMapper.class }*/)
public interface CustomerMapper {
    @Mapping(target = "id", expression = "java(java.util.UUID.randomUUID())")
    @Mapping(target = "loyaltyScore", constant = "0")
    @Mapping(target = "user", source = "user")
    Customer toEntity(CustomerRegistrationRequest request, User user);

    CustomerProfileResponse toProfileResponse(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromRequest(UpdateCustomerProfileRequest request, @MappingTarget Customer customer);
}
