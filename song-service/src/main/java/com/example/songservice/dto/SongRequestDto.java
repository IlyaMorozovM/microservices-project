package com.example.songservice.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongRequestDto {

    @NotNull(message = "Id is required")
    private Long id;

    @NotNull(message = "Name is required")
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    private String name;

    @NotNull(message = "Artist is required")
    @Size(min = 1, max = 100, message = "Artist must be between 1 and 100 characters")
    private String artist;

    @NotNull(message = "Album is required")
    @Size(min = 1, max = 100, message = "Album must be between 1 and 100 characters")
    private String album;

    @NotNull(message = "Duration is required")
    @Pattern(regexp = "^([0-5][0-9]):([0-5][0-9])$",
            message = "Duration must be in mm:ss format with leading zeros")
    private String duration;

    @NotNull(message = "Year is required")
    @Pattern(regexp = "^(19|20)\\d{2}$",
            message = "Year must be between 1900 and 2099")
    private String year;
}