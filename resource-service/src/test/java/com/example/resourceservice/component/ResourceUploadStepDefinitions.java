package com.example.resourceservice.component;

import com.example.resourceservice.messaging.ResourceEventPublisher;
import com.example.resourceservice.service.S3StorageService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class ResourceUploadStepDefinitions {

    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("resource_cucumber_test_db")
                    .withUsername("test_user")
                    .withPassword("test_pass");

    static {
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void configureDataSource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);

        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private S3StorageService s3StorageService;

    @MockitoBean
    private ResourceEventPublisher resourceEventPublisher;

    private MockMvc mockMvc;
    private MvcResult result;

    @Before
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Given("the storage service is available")
    public void theStorageServiceIsAvailable() {
        when(s3StorageService.upload(any()))
                .thenReturn("resources/generated-key.mp3");
    }

    @When("the client uploads a valid MP3 file")
    public void theClientUploadsAValidMp3File() throws Exception {
        byte[] fakeAudioData = new byte[]{1, 2, 3, 4, 5};

        result = mockMvc.perform(post("/resources")
                        .contentType("audio/mpeg")
                        .content(fakeAudioData))
                .andReturn();
    }

    @When("the client uploads a file with content type {string}")
    public void theClientUploadsAFileWithContentType(String contentType) throws Exception {
        byte[] fakeData = new byte[]{1, 2, 3};

        result = mockMvc.perform(post("/resources")
                        .contentType(contentType)
                        .content(fakeData))
                .andReturn();
    }

    @Then("the response status should be {int}")
    public void theResponseStatusShouldBe(int expectedStatus) {
        assertThat(result.getResponse().getStatus())
                .isEqualTo(expectedStatus);
    }

    @And("the response should contain a generated resource id")
    public void theResponseShouldContainAGeneratedResourceId() throws Exception {
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains("\"id\"");
    }

    @And("the response should contain an error message about invalid file format")
    public void theResponseShouldContainAnErrorMessageAboutInvalidFileFormat() throws Exception {
        String content = result.getResponse().getContentAsString();

        assertThat(content).contains("Invalid file format");
    }

    @And("a resource created event should be published")
    public void aResourceCreatedEventShouldBePublished() {
        org.mockito.Mockito.verify(resourceEventPublisher)
                .publishResourceCreated(org.mockito.ArgumentMatchers.anyLong());
    }
}