Feature: Resource upload
  As a client of the Resource Service
  I want to upload MP3 files
  So that they are stored and tracked by the system

  Scenario: Successfully upload a valid MP3 resource
    Given the storage service is available
    When the client uploads a valid MP3 file
    Then the response status should be 200
    And the response should contain a generated resource id

  Scenario: Reject upload when content type is invalid
    When the client uploads a file with content type "application/json"
    Then the response status should be 400
    And the response should contain an error message about invalid file format