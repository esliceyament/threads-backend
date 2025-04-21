package com.threads.feedservice.mapper;

import com.threads.feedservice.dto.FeedItemDto;
import com.threads.feedservice.entity.FeedItem;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface FeedMapper {
    FeedItemDto toDto(FeedItem feedItem);
}
