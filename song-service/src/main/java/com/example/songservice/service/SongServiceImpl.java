package com.example.songservice.service;

import com.example.songservice.dto.SongRequestDto;
import com.example.songservice.dto.SongResponseDto;
import com.example.songservice.entity.Song;
import com.example.songservice.exception.BadRequestException;
import com.example.songservice.exception.ConflictException;
import com.example.songservice.exception.ResourceNotFoundException;
import com.example.songservice.mapper.SongMapper;
import com.example.songservice.repository.SongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SongServiceImpl implements SongService {

    private static final int MAX_CSV_LENGTH = 200;

    private final SongRepository songRepository;
    private final SongMapper songMapper;

    @Override
    @Transactional
    public Long createSong(SongRequestDto dto) {
        if (songRepository.existsById(dto.getId())) {
            throw new ConflictException("Song metadata with ID=" + dto.getId() + " already exists");
        }
        Song song = songMapper.toEntity(dto);
        Song saved = songRepository.save(song);
        return saved.getId();
    }

    @Override
    public SongResponseDto getSongById(Long id) {
        validateId(id);
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song metadata with ID=" + id + " not found"));
        return songMapper.toResponseDto(song);
    }

    @Override
    @Transactional
    public List<Long> deleteSongsByIds(String csv) {
        List<Long> ids = parseAndValidateCsv(csv);
        List<Song> existingSongs = songRepository.findAllByIdIn(ids);
        List<Long> existingIds = existingSongs.stream().map(Song::getId).toList();
        songRepository.deleteAll(existingSongs);
        return existingIds;
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new BadRequestException("Id must be a positive number");
        }
    }

    private List<Long> parseAndValidateCsv(String csv) {
        if (csv == null || csv.isBlank()) {
            throw new BadRequestException("Id list must not be empty");
        }
        if (csv.length() > MAX_CSV_LENGTH) {
            throw new BadRequestException("Id list length must not exceed " + MAX_CSV_LENGTH + " characters");
        }

        String[] parts = csv.split(",");
        List<Long> ids = new ArrayList<>();
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.matches("\\d+")) {
                throw new BadRequestException("Id list contains invalid value: '" + trimmed + "'");
            }
            ids.add(Long.parseLong(trimmed));
        }
        return ids;
    }
}