package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(source = "unSaveUser.avatar.relativeUri", target = "avatarUrl")
    UserResponse userToUserResponse(User unSaveUser);

    User unSaveUserToUser(UnSaveUser unSaveUser);

    User updatedUserToUser(UpdatedUser updatedUser);
}
