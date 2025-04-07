package com.threads.userservice.mapper;

import com.threads.userservice.dto.UserProfileDto;
import com.threads.userservice.entity.UserProfile;
import com.threads.userservice.payload.UserProfileRequest;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, unmappedTargetPolicy = ReportingPolicy.IGNORE, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserProfileMapper {

    UserProfile toEntity(UserProfileRequest request);

    UserProfileDto toDto(UserProfile userProfile);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProfile(UserProfileDto dto, @MappingTarget UserProfile profile);
}
