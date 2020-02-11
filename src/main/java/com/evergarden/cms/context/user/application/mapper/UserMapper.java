package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.User;
import com.evergarden.cms.context.user.infrastructure.controller.input.UnSaveUser;
import com.evergarden.cms.context.user.infrastructure.controller.input.UpdatedUser;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper
// @Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(
        source = "user.avatar.relativeUri",
        target = "avatarUrl",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.SET_TO_DEFAULT
    )
    UserResponse userToUserResponse(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "avatar", ignore = true)
    @Mapping(target = "encodedCredential", ignore = true)
    User unSaveUserToUser(UnSaveUser unSaveUser);
    
    @Mapping(target = "encodedCredential", ignore = true)
    User updatedUserToUser(UpdatedUser updatedUser);
}
