package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.infrastructure.controller.output.AvatarResponse;
import com.evergarden.cms.context.user.infrastructure.controller.output.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AvatarMapper {

    AvatarMapper INSTANCE = Mappers.getMapper(AvatarMapper.class);

    @Mapping(source = "user.avatarUrl", target = "avatarUri")
    AvatarResponse userResponseToAvatarResponse(UserResponse user);
}
