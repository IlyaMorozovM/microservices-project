package com.example.resourceservice.repository;

import com.example.resourceservice.entity.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ResourceRepositoryIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("resource_test_db")
            .withUsername("test_user")
            .withPassword("test_pass");

    @DynamicPropertySource
    static void overrideProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @org.springframework.beans.factory.annotation.Autowired
    private ResourceRepository resourceRepository;

    @Test
    void findAllByIdIn_shouldReturnOnlyMatchingResources() {
        Resource resource1 = new Resource();
        resource1.setStorageKey("resources/key1.mp3");
        Resource saved1 = resourceRepository.save(resource1);

        Resource resource2 = new Resource();
        resource2.setStorageKey("resources/key2.mp3");
        Resource saved2 = resourceRepository.save(resource2);

        Resource resource3 = new Resource();
        resource3.setStorageKey("resources/key3.mp3");
        resourceRepository.save(resource3);

        List<Resource> result = resourceRepository.findAllByIdIn(
                List.of(saved1.getId(), saved2.getId()));

        assertThat(result)
                .hasSize(2)
                .extracting(Resource::getStorageKey)
                .containsExactlyInAnyOrder("resources/key1.mp3", "resources/key2.mp3");
    }

    @Test
    void save_shouldPersistResourceWithGeneratedId() {
        Resource resource = new Resource();
        resource.setStorageKey("resources/new-key.mp3");

        Resource saved = resourceRepository.save(resource);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getStorageKey()).isEqualTo("resources/new-key.mp3");
    }
}