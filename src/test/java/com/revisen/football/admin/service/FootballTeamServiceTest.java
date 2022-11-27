package com.revisen.football.admin.service;

import com.revisen.football.admin.entity.FootballPlayerEntity;
import com.revisen.football.admin.entity.FootballTeamEntity;
import com.revisen.football.admin.repository.FootballTeamRepository;
import com.revisen.football.admin.request.FootballPlayerRequest;
import com.revisen.football.admin.request.FootballTeamRequest;
import com.revisen.football.admin.response.FootballPlayerResponse;
import com.revisen.football.admin.response.FootballTeamResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.junit.jupiter.api.Assertions.assertTrue;
import  static org.mockito.Mockito.verify;

class FootballTeamServiceTest {

    FootballTeamService footballTeamService;

    @Mock
    FootballTeamRepository footballTeamRepository;
    @Mock
    FootballPlayerService footballPlayerService;

    @BeforeEach
    void setUp() {
        openMocks(this);
        footballTeamService = new FootballTeamService(footballTeamRepository, footballPlayerService);
    }

    @Test
    void shouldSaveTeamWithNoPlayers() {
        //Arrange
        FootballTeamRequest teamRequest = frameFootballTeamRequest();

        //Act
        ResponseEntity<FootballTeamResponse> teamResponse = footballTeamService.saveTeam(teamRequest);

        //Assert
        assertTrue(teamResponse.hasBody());
        assertEquals(HttpStatus.CREATED, teamResponse.getStatusCode());
        verify(footballTeamRepository).save(any());
        verify(footballPlayerService, times(0)).savePlayer(any(FootballPlayerEntity.class));
        verify(footballPlayerService, times(0)).deletePlayerById(any());
    }


    @Test
    void shouldSaveTeamWithPlayers() {
        //Arrange
        FootballPlayerRequest playerRequest = frameFootballPlayerRequest();
        FootballTeamRequest teamRequest = frameFootballTeamRequest();
        teamRequest.setFootballPlayerRequests(Collections.singletonList(playerRequest));

        ResponseEntity<FootballPlayerResponse> playerResponseEntity = ResponseEntity.ok(
                FootballPlayerResponse.builder()
                        .playerData(FootballPlayerResponse.PlayerData.builder()
                                .footballPlayerEntityList(
                                        Collections.singletonList(FootballPlayerEntity.builder().build()))
                                .build())
                        .build()
        );

        when(footballPlayerService.savePlayer(any(FootballPlayerEntity.class))).thenReturn(playerResponseEntity);

        //Act
        ResponseEntity<FootballTeamResponse> teamResponse = footballTeamService.saveTeam(teamRequest);

        //Assert
        assertTrue(teamResponse.hasBody());
        assertEquals(HttpStatus.CREATED, teamResponse.getStatusCode());
        verify(footballTeamRepository).save(any());
        verify(footballPlayerService, times(1)).savePlayer(any(FootballPlayerEntity.class));
        verify(footballPlayerService, times(0)).deletePlayerById(any());
    }

    @Test
    void shouldGetTeamsWithNoPlayers() {

        //Arrange
        List<FootballTeamEntity> teamEntities = Collections.singletonList(frameFootballTeamEntity());

        when(footballTeamRepository.findAll()).thenReturn(teamEntities);
        when(footballPlayerService.getPlayerByIdTeam(any())).thenReturn(
                ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));

        //Act
        ResponseEntity<FootballTeamResponse> teamResponse = footballTeamService.getTeams();

        //Assert
        assertTrue(teamResponse.hasBody());
        assertEquals(HttpStatus.OK, teamResponse.getStatusCode());
        verify(footballTeamRepository).findAll();
        verify(footballPlayerService).getPlayerByIdTeam(any());
    }

    @Test
    void shouldGetTeamsWithPlayers() {
        //Arrange
        String idTeam = "01idTeam1234";
        List<FootballTeamEntity> teamEntities = Collections.singletonList(frameFootballTeamEntity(idTeam));
        FootballPlayerEntity playerEntity = frameFootballPlayerEntity(idTeam);

        ResponseEntity<FootballPlayerResponse> playerResponse = ResponseEntity.ok(FootballPlayerResponse.builder()
                .playerData(FootballPlayerResponse.PlayerData.builder()
                        .footballPlayerEntityList(Collections.singletonList(playerEntity))
                        .build()
                ).build());

        when(footballTeamRepository.findAll()).thenReturn(teamEntities);
        when(footballPlayerService.getPlayerByIdTeam(any())).thenReturn(playerResponse);

        //Act
        ResponseEntity<FootballTeamResponse> teamResponse = footballTeamService.getTeams();

        //Assert
        assertTrue(teamResponse.hasBody());
        assertEquals(HttpStatus.OK, teamResponse.getStatusCode());
        verify(footballTeamRepository).findAll();
        verify(footballPlayerService).getPlayerByIdTeam(any());

        assertEquals(idTeam, teamResponse.getBody().getTeamData().getFootballTeamEntities().get(0)
                .getFootballPlayerEntities().get(0).getIdTeam());
    }

    @Test
    void shouldGetTeamById() {
        //Arrange
        String idTeam = "01idTeam1234";

        Optional<FootballTeamEntity> optionalTeamEntity = Optional.of(frameFootballTeamEntity(idTeam));
        FootballPlayerEntity playerEntity = frameFootballPlayerEntity(idTeam);

        ResponseEntity<FootballPlayerResponse> playerResponse = ResponseEntity.ok(FootballPlayerResponse.builder()
                .playerData(FootballPlayerResponse.PlayerData.builder()
                        .footballPlayerEntityList(Collections.singletonList(playerEntity))
                        .build()
                ).build());

        when(footballTeamRepository.findById(any())).thenReturn(optionalTeamEntity);
        when(footballPlayerService.getPlayerByIdTeam(any())).thenReturn(playerResponse);

        //Act
        ResponseEntity<FootballTeamResponse> teamResponse = footballTeamService.getTeamById(idTeam);

        //Assert
        assertTrue(teamResponse.hasBody());
        assertEquals(HttpStatus.OK, teamResponse.getStatusCode());
        verify(footballTeamRepository).findById(any());
        verify(footballPlayerService).getPlayerByIdTeam(any());
        assertEquals(idTeam, teamResponse.getBody().getTeamData().getFootballTeamEntities().get(0)
                .getFootballPlayerEntities().get(0).getIdTeam());
    }

    @Test
    void shouldDeleteTeamByIdWithNoPlayers() {
        //Arrange
        String idTeam = "01id1234";

        Optional<FootballTeamEntity> optionalTeamEntity = Optional.of(frameFootballTeamEntity(idTeam));
        ResponseEntity<FootballPlayerResponse> playerResponse = ResponseEntity.ok(FootballPlayerResponse.builder()
                .playerData(FootballPlayerResponse.PlayerData.builder()
                        .build()
                ).build());

        when(footballPlayerService.getPlayerByIdTeam(any())).thenReturn(playerResponse);
        when(footballTeamRepository.findById(any())).thenReturn(optionalTeamEntity);

        //Act
        footballTeamService.deleteTeamById(idTeam);

        //Assert
        verify(footballTeamRepository).deleteById(any());
        verify(footballPlayerService, times(0)).deletePlayerById(any());
    }

    @Test
    void shouldDeleteTeamByIdWithPlayers() {
        //Arrange
        String idTeam = "01id1234";

        Optional<FootballTeamEntity> optionalTeamEntity = Optional.of(frameFootballTeamEntity(idTeam));
        ResponseEntity<FootballPlayerResponse> playerResponse = ResponseEntity.ok(FootballPlayerResponse.builder()
                .playerData(FootballPlayerResponse.PlayerData.builder()
                        .footballPlayerEntityList(Collections.singletonList(frameFootballPlayerEntity()))
                        .build()
                ).build());

        when(footballPlayerService.getPlayerByIdTeam(any())).thenReturn(playerResponse);
        when(footballTeamRepository.findById(any())).thenReturn(optionalTeamEntity);

        //Act
        footballTeamService.deleteTeamById(idTeam);

        //Assert
        verify(footballTeamRepository).deleteById(any());
        verify(footballPlayerService).deletePlayerById(any());
    }

    @Test
    void completeUpdateTeamById() {
    }

    @Test
    void partialUpdateTeamById() {
    }

    private FootballTeamRequest frameFootballTeamRequest() {
        return FootballTeamRequest.builder()
                .name("name")
                .country("country")
                .wins(23)
                .build();
    }

    private FootballPlayerRequest frameFootballPlayerRequest() {
        return FootballPlayerRequest.builder()
                .name("playerName")
                .age(99)
                .rate(99.9)
                .build();
    }

    private FootballTeamEntity frameFootballTeamEntity() {
        return frameFootballTeamEntity("01idTeam1234");
    }

    private FootballTeamEntity frameFootballTeamEntity(String idTeam) {
        return FootballTeamEntity.builder()
                .id(idTeam)
                .name("name")
                .wins(99)
                .build();
    }

    private FootballPlayerEntity frameFootballPlayerEntity() {
        return frameFootballPlayerEntity("01idTeam1234");
    }

    private FootballPlayerEntity frameFootballPlayerEntity(String idTeam) {
        return FootballPlayerEntity.builder()
                .idTeam(idTeam)
                .name("playerName")
                .age(99)
                .rate(99.9)
                .build();
    }
}