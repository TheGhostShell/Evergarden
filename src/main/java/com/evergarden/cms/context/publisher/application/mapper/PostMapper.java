package com.evergarden.cms.context.publisher.application.mapper;

import com.evergarden.cms.context.publisher.domain.entity.Post;
import com.evergarden.cms.context.publisher.infrastructure.controller.input.UnSavePostRequest;
import com.evergarden.cms.context.publisher.infrastructure.controller.output.PostSummaryResponse;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface PostMapper {
    PostMapper INSTANCE = Mappers.getMapper(PostMapper.class);

    Post unSavePostToPost(UnSavePostRequest unSavePost);

    PostSummaryResponse toPostSummaryResponse(Post post);
}
