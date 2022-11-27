package com.revisen.football.admin.service;

import com.revisen.football.admin.entity.FootballPlayerEntity;
import com.revisen.football.admin.repository.FootballPlayerRepository;
import com.revisen.football.admin.request.FootballPlayerRequest;
import com.revisen.football.admin.response.FootballPlayerResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import  static org.mockito.Mockito.verify;


class FootballPlayerServiceTest {

    FootballPlayerService footballPlayerService;

    @Mock
    FootballPlayerRepository footballPlayerRepository;

    @BeforeEach
    void setUp() {
        openMocks(this);
        footballPlayerService = new FootballPlayerService(footballPlayerRepository);
    }

    @Test
    void shouldSavePlayer() {
        //Arrange
        FootballPlayerRequest playerRequest = frameFootballPlayerRequest();
        FootballPlayerEntity savedPlayer = frameFootballPlayerEntity();
        when(footballPlayerRepository.save(any())).thenReturn(savedPlayer);

        //Act
        ResponseEntity<FootballPlayerResponse> playerResponse = footballPlayerService.savePlayer(playerRequest);

        //Assert
        verify(footballPlayerRepository).save(any());
        assertNotNull(playerResponse.getBody());
        assertEquals(HttpStatus.CREATED, playerResponse.getStatusCode());
    }

    @Test
    void shouldGetPlayers() {
        //Arrange
        List<FootballPlayerEntity> playerEntities = Collections.singletonList(frameFootballPlayerEntity());
        when(footballPlayerRepository.findAll()).thenReturn(playerEntities);

        //Act
        ResponseEntity<FootballPlayerResponse> allPlayers = footballPlayerService.getAllPlayers();

        //Assert
        verify(footballPlayerRepository).findAll();
        assertNotNull(allPlayers.getBody());
        assertEquals(HttpStatus.OK, allPlayers.getStatusCode());
    }

    @Test
    void shouldNoGetPlayers() {
        //Arrange
        when(footballPlayerRepository.findAll()).thenReturn(Collections.emptyList());

        //Act
        ResponseEntity<FootballPlayerResponse> allPlayers = footballPlayerService.getAllPlayers();

        //Assert
        verify(footballPlayerRepository).findAll();
        assertTrue(allPlayers.getBody().getPlayerData().getFootballPlayerEntityList().isEmpty());
        assertEquals(HttpStatus.OK, allPlayers.getStatusCode());
    }

    @Test
    void shouldNoGetPlayerById() {
        //Arrange
        String idPlayer = "unknown";
        Optional<FootballPlayerEntity> optional = Optional.empty();
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> player = footballPlayerService.getPlayerById(idPlayer);

        //Assert
        verify(footballPlayerRepository).findById(any());
        assertEquals(HttpStatus.NOT_FOUND, player.getStatusCode());
    }

    @Test
    void shouldGetPlayerById() {
        //Arrange
        String idPlayer = "idPlayer123";
        Optional<FootballPlayerEntity> optional = Optional.of(frameFootballPlayerEntity(idPlayer));
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> player = footballPlayerService.getPlayerById(idPlayer);

        //Assert
        verify(footballPlayerRepository).findById(any());
        assertNotNull(player.getBody());
        assertEquals(HttpStatus.OK, player.getStatusCode());
    }

    @Test
    void shouldGetPlayerByIdTeam() {
        //Arrange
        String idTeam = "idTeam123";
        FootballPlayerEntity playerEntity = frameFootballPlayerEntity();
        playerEntity.setIdTeam(idTeam);
        when(footballPlayerRepository.findByIdTeam(any())).thenReturn(Collections.singletonList(playerEntity));

        //Act
        ResponseEntity<FootballPlayerResponse> player = footballPlayerService.getPlayerByIdTeam(idTeam);

        //Assert
        verify(footballPlayerRepository).findByIdTeam(any());
        assertNotNull(player.getBody());
        assertEquals(HttpStatus.OK, player.getStatusCode());
        assertEquals(idTeam, player.getBody().getPlayerData().getFootballPlayerEntityList().get(0).getIdTeam());
    }

    @Test
    void shouldDeletePlayerById() {
        //Arrange
        String idPlayer = "01IdPlayer123";
        Optional<FootballPlayerEntity> optional = Optional.of(frameFootballPlayerEntity(idPlayer));
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> player = footballPlayerService.deletePlayerById(idPlayer);

        //Assert
        verify(footballPlayerRepository).deleteById(any());
        assertEquals(HttpStatus.OK, player.getStatusCode());
    }

    @Test
    void shouldNoDeletePlayerById() {
        //Arrange
        String idPlayer = "01IdPlayer123";
        Optional<FootballPlayerEntity> optional = Optional.empty();
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> player = footballPlayerService.deletePlayerById(idPlayer);

        //Assert
        verify(footballPlayerRepository, times(0)).deleteById(any());
        assertEquals(HttpStatus.NO_CONTENT, player.getStatusCode());
    }

    @Test
    void shouldUpdatePlayerById() {
        //Arrange
        String idPlayer = "01idPlayer123";
        FootballPlayerRequest request = frameFootballPlayerRequest();
        Optional<FootballPlayerEntity> optional = Optional.of(frameFootballPlayerEntity(idPlayer));
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> updatedPlayer =
                footballPlayerService.updatePlayerById(request, idPlayer);

        //Assert
        assertEquals(HttpStatus.OK, updatedPlayer.getStatusCode());
        verify(footballPlayerRepository).save(any());
    }

    @Test
    void shouldNoUpdatePlayerById() {
        //Arrange
        String idPlayer = "01idPlayer123";
        FootballPlayerRequest request = frameFootballPlayerRequest();
        Optional<FootballPlayerEntity> optional = Optional.empty();
        when(footballPlayerRepository.findById(any())).thenReturn(optional);

        //Act
        ResponseEntity<FootballPlayerResponse> updatedPlayer =
                footballPlayerService.updatePlayerById(request, idPlayer);

        //Assert
        assertEquals(HttpStatus.NO_CONTENT, updatedPlayer.getStatusCode());
        verify(footballPlayerRepository,times(0)).save(any());
    }

    private FootballPlayerRequest frameFootballPlayerRequest() {
        return FootballPlayerRequest.builder()
                .name("name")
                .age(99)
                .rate(99.9)
                .build();
    }

    private FootballPlayerEntity frameFootballPlayerEntity() {
        return frameFootballPlayerEntity("id123");
    }

    private FootballPlayerEntity frameFootballPlayerEntity(String id) {
        return FootballPlayerEntity.builder()
                .id(id)
                .name("name")
                .age(99)
                .rate(99.9)
                .build();
    }
}