package com.example.songservice.controller;

import com.example.songservice.dto.IdResponseDto;
import com.example.songservice.dto.IdsResponseDto;
import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.service.SongService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SongController {

    private final SongService songService;

    @PostMapping("/songs")
    public ResponseEntity<IdResponseDto> createSong(@Valid @RequestBody SongRequestDto dto) {
        Long id = songService.createSong(dto);
        return ResponseEntity.ok(new IdResponseDto(id));
    }

    @GetMapping("/songs/{id}")
    public ResponseEntity<SongResponseDto> getSong(@PathVariable Long id) {
        SongResponseDto song = songService.getSongById(id);
        return ResponseEntity.ok(song);
    }

    @DeleteMapping("/songs")
    public ResponseEntity<IdsResponseDto> deleteSongs(@RequestParam("id") String ids) {
        var deletedIds = songService.deleteSongsByIds(ids);
        return ResponseEntity.ok(new IdsResponseDto(deletedIds));
    }
}