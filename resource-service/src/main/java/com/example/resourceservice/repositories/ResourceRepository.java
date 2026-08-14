package com.example.resourceservice.repositories;

import com.example.resourceservice.entities.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}
