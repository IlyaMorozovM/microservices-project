package com.example.e2e;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.Duration;
import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

public class ResourceE2EStepDefinitions {

    private static final String RESOURCE_SERVICE_URL = "http://localhost:8081/resources";
    private static final String SONG_DB_URL = "jdbc:postgresql://localhost:5434/song_service_db";
    private static final String SONG_DB_USER = "song_user";
    private static final String SONG_DB_PASSWORD = "song_pass";

    private final RestTemplate restTemplate = new RestTemplate();
    private int songsCountBefore;

    @Given("the microservices stack is running")
    public void theMicroservicesStackIsRunning() {
        songsCountBefore = countSongs();
    }

    @When("the client uploads a valid MP3 file to the Resource Service")
    public void theClientUploadsAValidMp3File() throws Exception {
        byte[] audioData;
        try (InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("files/sample.mp3")) {
            if (inputStream == null) {
                throw new IllegalStateException("Test file files/sample.mp3 not found on classpath");
            }
            audioData = inputStream.readAllBytes();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("audio/mpeg"));
        HttpEntity<byte[]> request = new HttpEntity<>(audioData, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                RESOURCE_SERVICE_URL, request, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
    }

    @Then("a new song metadata record should eventually appear in the Song Service database")
    public void aNewSongMetadataRecordShouldEventuallyAppear() {
        Instant deadline = Instant.now().plus(Duration.ofSeconds(20));
        boolean increased = false;

        while (Instant.now().isBefore(deadline)) {
            if (countSongs() > songsCountBefore) {
                increased = true;
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        assertThat(increased)
                .withFailMessage("No new song record appeared within timeout")
                .isTrue();
    }

    private int countSongs() {
        try (Connection connection = DriverManager.getConnection(SONG_DB_URL, SONG_DB_USER, SONG_DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM songs")) {

            resultSet.next();
            return resultSet.getInt(1);
        } catch (Exception e) {
            throw new RuntimeException("Failed to query Song DB", e);
        }
    }
}