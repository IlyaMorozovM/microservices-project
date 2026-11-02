Feature: Resource processing end-to-end flow

  Scenario: Uploaded resource metadata becomes available in Song Service
    Given the microservices stack is running
    When the client uploads a valid MP3 file to the Resource Service
    Then a new song metadata record should eventually appear in the Song Service database