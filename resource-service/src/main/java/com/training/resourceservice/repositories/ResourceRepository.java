package com.training.resourceservice.repositories;

import com.training.resourceservice.entities.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResourceRepository extends JpaRepository<ResourceEntity, Long> {
}
