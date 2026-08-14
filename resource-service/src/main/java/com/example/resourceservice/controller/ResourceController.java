package com.example.resourceservice.controller;

import com.example.resourceservice.dto.IdResponseDto;
import com.example.resourceservice.dto.IdsResponseDto;
import com.example.resourceservice.service.ResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    @PostMapping(value = "/resources", consumes = "audio/mpeg")
    public ResponseEntity<IdResponseDto> uploadResource(@RequestBody byte[] audioData) {
        Long id = resourceService.uploadResource(audioData);
        return ResponseEntity.ok(new IdResponseDto(id));
    }

    @GetMapping(value = "/resources/{id}", produces = "audio/mpeg")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        byte[] audioData = resourceService.getResourceById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("audio/mpeg"))
                .body(audioData);
    }

    @DeleteMapping("/resources")
    public ResponseEntity<IdsResponseDto> deleteResources(@RequestParam("id") String ids) {
        var deletedIds = resourceService.deleteResourcesByIds(ids);
        return ResponseEntity.ok(new IdsResponseDto(deletedIds));
    }
}