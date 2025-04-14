package com.threads.postservice.mapper;

import com.threads.postservice.dto.PostDto;
import com.threads.postservice.dto.PostUpdateDto;
import com.threads.postservice.dto.ReplyUpdateDto;
import com.threads.postservice.entity.Post;
import com.threads.postservice.payload.PostRequest;
import com.threads.postservice.payload.ReplyRequest;
import com.threads.postservice.response.PostResponse;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PostMapper {

    Post toEntity(PostRequest request);
    PostDto toDto(Post post);

    Post toEntity(ReplyRequest request);
    PostDto toDtoReply(Post post);

    PostResponse toResponse(Post post);

    Post toRepost(Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePost(PostUpdateDto dto, @MappingTarget Post post);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateReply(ReplyUpdateDto dto, @MappingTarget Post post);
}
