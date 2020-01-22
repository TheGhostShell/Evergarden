package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(
        source = "user.avatar.relativeUri",
        target = "avatarUrl",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
    )
    UserResponse userToUserResponse(User user);

    User unSaveUserToUser(UnSaveUser unSaveUser);

    User updatedUserToUser(UpdatedUser updatedUser);
}