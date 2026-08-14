package com.example.resourceservice.mapper;

import com.example.resourceservice.dto.IdResponseDto;
import com.example.resourceservice.entity.Resource;
import org.springframework.stereotype.Component;

@Component
public class ResourceMapper {

    public IdResponseDto toIdResponseDto(Resource resource) {
        return new IdResponseDto(resource.getId());
    }
}