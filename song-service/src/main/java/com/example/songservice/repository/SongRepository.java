package com.example.songservice.repository;

import com.example.songservice.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {

    List<Song> findAllByIdIn(List<Long> ids);
}