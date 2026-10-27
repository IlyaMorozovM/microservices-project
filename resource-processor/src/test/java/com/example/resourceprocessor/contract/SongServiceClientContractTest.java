package com.example.resourceprocessor.contract;

import com.example.resourceprocessor.client.SongServiceClient;
import com.example.resourceprocessor.dto.SongMetadataDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.stubrunner.spring.AutoConfigureStubRunner;
import org.springframework.cloud.contract.stubrunner.spring.StubRunnerProperties;

import static org.assertj.core.api.Assertions.assertThatCode;

@SpringBootTest(properties = {
        "eureka.client.enabled=false",
        "spring.rabbitmq.listener.simple.auto-startup=false",
        "spring.cloud.discovery.client.simple.instances.song-service[0].uri=http://localhost:8090"
})
@AutoConfigureStubRunner(
        ids = "com.example:song-service:0.0.1:stubs:8090",
        stubsMode = StubRunnerProperties.StubsMode.LOCAL)
class SongServiceClientContractTest {

    @Autowired
    private SongServiceClient songServiceClient;

    @Test
    void createSongMetadata_shouldSucceed_whenRequestMatchesContract() {
        SongMetadataDto metadata = new SongMetadataDto(
                1L, "We are the champions", "Queen",
                "News of the world", "02:59", "1977");

        assertThatCode(() -> songServiceClient.createSongMetadata(metadata))
                .doesNotThrowAnyException();
    }
}