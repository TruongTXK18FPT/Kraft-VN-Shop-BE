package com.mss301.kraft.media_service.mapper;

import com.mss301.kraft.media_service.dto.MediaResponse;
import com.mss301.kraft.media_service.entity.CloudinaryMedia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface MediaMapper {

    @Mapping(target = "createdAt", expression = "java(toLocalDateTime(media.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(toLocalDateTime(media.getUpdatedAt()))")
    MediaResponse toResponse(CloudinaryMedia media);

    default LocalDateTime toLocalDateTime(OffsetDateTime offsetDateTime) {
        if (offsetDateTime == null) {
            return null;
        }
        return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
    }
}
