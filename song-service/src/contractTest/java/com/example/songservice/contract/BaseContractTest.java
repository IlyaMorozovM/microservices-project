package com.example.songservice.contract;

import com.example.songservice.controller.SongController;
import com.example.songservice.service.SongService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class BaseContractTest {

    @BeforeEach
    public void setup() {
        SongService songService = Mockito.mock(SongService.class);
        when(songService.createSong(any())).thenReturn(1L);

        RestAssuredMockMvc.standaloneSetup(new SongController(songService));
    }
}