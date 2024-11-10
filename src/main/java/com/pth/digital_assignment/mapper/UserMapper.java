package com.pth.digital_assignment.mapper;

import com.pth.digital_assignment.dto.user.InternalUserResponse;
import com.pth.digital_assignment.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    InternalUserResponse toInternalUserResponse(User user);
}
