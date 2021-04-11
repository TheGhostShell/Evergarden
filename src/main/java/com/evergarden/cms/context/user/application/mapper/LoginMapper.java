package com.evergarden.cms.context.user.application.mapper;

import com.evergarden.cms.context.user.domain.entity.Token;
import com.evergarden.cms.context.user.infrastructure.controller.output.LoginResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LoginMapper {
    LoginMapper INSTANCE = Mappers.getMapper(LoginMapper.class);

    @Mapping(source = "token.tokenString", target = "token")
    LoginResponse tokenToLoginResponse(Token token);
}
