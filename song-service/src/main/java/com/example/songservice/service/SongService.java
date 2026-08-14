package com.example.songservice.service;

import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;

import java.util.List;

public interface SongService {

    Long createSong(SongRequestDto dto);

    SongResponseDto getSongById(Long id);

    List<Long> deleteSongsByIds(String csv);
}