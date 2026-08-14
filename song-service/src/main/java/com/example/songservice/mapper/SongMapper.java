package com.example.songservice.mapper;

import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.entity.Song;
import org.springframework.stereotype.Component;

@Component
public class SongMapper {

    public Song toEntity(SongRequestDto dto) {
        Song song = new Song();
        song.setId(dto.getId());
        song.setName(dto.getName());
        song.setArtist(dto.getArtist());
        song.setAlbum(dto.getAlbum());
        song.setDuration(dto.getDuration());
        song.setYear(dto.getYear());
        return song;
    }

    public SongResponseDto toResponseDto(Song song) {
        return new SongResponseDto(
                song.getId(),
                song.getName(),
                song.getArtist(),
                song.getAlbum(),
                song.getDuration(),
                song.getYear()
        );
    }
}