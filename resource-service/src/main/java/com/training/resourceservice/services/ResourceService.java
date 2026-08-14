package com.training.resourceservice.services;

import com.training.resourceservice.entities.ResourceEntity;
import com.training.resourceservice.repositories.ResourceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;

    public ResourceService(ResourceRepository resourceRepository) {
        this.resourceRepository = resourceRepository;
    }

    public ResourceEntity saveResource(byte[] audioData) {
        ResourceEntity resource = new ResourceEntity();
        resource.setAudioData(audioData);
        return resourceRepository.save(resource);
    }

    public Optional<ResourceEntity> getResourceById(Long id) {
        return resourceRepository.findById(id);
    }

    public void deleteResourceById(Long id) {
        resourceRepository.deleteById(id);
    }
}