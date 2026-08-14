package com.example.resourceservice.service;

import java.util.List;

public interface ResourceService {

    Long uploadResource(byte[] audioData);

    byte[] getResourceById(Long id);

    List<Long> deleteResourcesByIds(String csv);
}