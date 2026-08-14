package com.example.controllers;

import com.example.resourceservice.entities.ResourceEntity;
import com.example.resourceservice.services.ResourceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<Long> uploadResource(@RequestParam("file") MultipartFile file) throws IOException {
        if (!file.getContentType().equals("audio/mpeg")) {
            return ResponseEntity.badRequest().build();
        }
        ResourceEntity resource = resourceService.saveResource(file.getBytes());
        return ResponseEntity.ok(resource.getId());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getResource(@PathVariable Long id) {
        return resourceService.getResourceById(id)
                .map(resource -> ResponseEntity.ok(resource.getAudioData()))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteResource(@RequestParam Long id) {
        resourceService.deleteResourceById(id);
        return ResponseEntity.ok("Resource deleted successfully.");
    }
}
