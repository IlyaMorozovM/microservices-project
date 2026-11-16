package com.example.storageservice.repository;

import com.example.storageservice.entity.Storage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageRepository extends JpaRepository<Storage, Long> {

    List<Storage> findAllByIdIn(List<Long> ids);

    boolean existsByBucketAndPath(String bucket, String path);
}