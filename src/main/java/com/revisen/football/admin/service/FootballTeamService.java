package com.revisen.football.admin.service;

import com.revisen.football.admin.entity.FootballPlayerEntity;
import com.revisen.football.admin.entity.FootballTeamEntity;
import com.revisen.football.admin.repository.FootballTeamRepository;
import com.revisen.football.admin.request.FootballPlayerRequest;
import com.revisen.football.admin.request.FootballTeamRequest;
import com.revisen.football.admin.response.FootballPlayerResponse;
import com.revisen.football.admin.response.FootballTeamResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FootballTeamService {

    @Autowired
    private final FootballTeamRepository footballTeamRepository;
    @Autowired
    private final FootballPlayerService footballPlayerService;

    public FootballTeamService(
            FootballTeamRepository footballTeamRepository, FootballPlayerService footballPlayerService) {
        this.footballTeamRepository = footballTeamRepository;
        this.footballPlayerService = footballPlayerService;
    }

    public ResponseEntity<FootballTeamResponse> saveTeam(FootballTeamRequest teamRequest) {
        FootballTeamEntity footballTeamEntity = frameFootballTeamEntity(teamRequest);
        return saveUpdateTeamAndPlayers(footballTeamEntity, teamRequest, false);
    }

    private ResponseEntity<FootballTeamResponse> saveTeam(FootballTeamEntity teamEntity) {
        footballTeamRepository.save(teamEntity);
        return frameResponseEntity(Collections.singletonList(teamEntity), HttpStatus.CREATED);
    }

    public ResponseEntity<FootballTeamResponse> getTeams() {
        log.info("Fetching Teams");
        List<FootballTeamEntity> footballTeams = footballTeamRepository.findAll();

        for (FootballTeamEntity footballTeamEntity : footballTeams) {
            footballTeamEntity.setFootballPlayerEntities(getFootballPlayers(footballTeamEntity));
        }
        return frameResponseEntity(footballTeams, HttpStatus.OK);
    }

    public ResponseEntity<FootballTeamResponse> getTeamById(String id) {
        log.info("Fetching Team by Id");
        Optional<FootballTeamEntity> footballTeam = footballTeamRepository.findById(id);

        if (footballTeam.isPresent()) {
            FootballTeamEntity footballTeamEntity = footballTeam.get();
            footballTeamEntity.setFootballPlayerEntities(getFootballPlayers(footballTeamEntity));

            return frameResponseEntity(Collections.singletonList(footballTeamEntity), HttpStatus.OK);
        }
        return frameResponseEntity(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<FootballTeamResponse> deleteTeamById(String id) {
        log.info("Deleting Team");
        ResponseEntity<FootballTeamResponse> footballTeam = getTeamById(id);

        if (footballTeam.hasBody() && HttpStatus.OK.equals(footballTeam.getStatusCode())) {
            footballTeamRepository.deleteById(id);
            deletePlayersFromTeam(footballTeam);

            return frameResponseEntity(HttpStatus.OK);
        }
        return frameResponseEntity(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<FootballTeamResponse> completeUpdateTeamById(
            String id, FootballTeamRequest incomingTeamRequest) {
        log.info("Updating Team");
        return updateTeamById(id, incomingTeamRequest, true);
    }

    public ResponseEntity<FootballTeamResponse> partialUpdateTeamById(
            String id, FootballTeamRequest incomingTeamRequest) {
        return  updateTeamById(id, incomingTeamRequest, false);
    }

    private ResponseEntity<FootballTeamResponse> updateTeamById(
            String id, FootballTeamRequest incomingTeamRequest, boolean isCompleteUpdate) {
        ResponseEntity<FootballTeamResponse> existingTeam = getTeamById(id);

        if (existingTeam.hasBody() && HttpStatus.OK.equals(existingTeam.getStatusCode())) {

            FootballTeamEntity existingTeamEntity =
                    existingTeam.getBody().getTeamData().getFootballTeamEntities().get(0);
            FootballTeamEntity updatedTeamEntity = updatePropertiesTeamEntity(incomingTeamRequest, existingTeamEntity);

            saveUpdateTeamAndPlayers(updatedTeamEntity, incomingTeamRequest, isCompleteUpdate);

            return frameResponseEntity(Collections.emptyList(), HttpStatus.OK);
        }
        return frameResponseEntity(HttpStatus.NO_CONTENT);
    }

    private ResponseEntity<FootballTeamResponse> saveUpdateTeamAndPlayers(
            FootballTeamEntity teamEntity, FootballTeamRequest teamRequest, boolean isCompleteUpdate) {
        log.info("Saving Teams");
        ResponseEntity<FootballTeamResponse> teamResponseEntity = saveTeam(teamEntity);

        if (isCompleteUpdate) {
            removePlayers(teamEntity.getId());
        }

        List<FootballPlayerEntity> playerEntities = saveFootballPlayers(teamRequest, teamEntity.getId());
        teamResponseEntity.getBody().getTeamData()
                .getFootballTeamEntities().get(0).setFootballPlayerEntities(playerEntities);

        return teamResponseEntity;
    }

    //Section to get the Football players using the external FootballPlayerService
    private void removePlayers(String idTeam) {
        ResponseEntity<FootballPlayerResponse> players = footballPlayerService.getPlayers(idTeam);

        if (players.hasBody() && HttpStatus.OK.equals(players.getStatusCode())) {
            List<FootballPlayerEntity> playerEntities = players.getBody().getPlayerData().getFootballPlayerEntityList();

            for (FootballPlayerEntity playerEntity : playerEntities) {
                footballPlayerService.deletePlayerById(playerEntity.getId());
            }
        }
    }

    private ResponseEntity<FootballPlayerResponse> saveFootballPlayer(FootballPlayerEntity playerEntity, String idTeam) {
        ResponseEntity<FootballPlayerResponse>  playerResponseEntity = null;
        if (Objects.nonNull(playerEntity)) {
            playerEntity.setIdTeam(idTeam);
            playerResponseEntity = footballPlayerService.savePlayer(playerEntity);
        }

        return playerResponseEntity;
    }

    private List<FootballPlayerEntity> saveFootballPlayers(FootballTeamRequest teamRequest, String idTeam) {
        List<FootballPlayerEntity> footballPlayers =
                frameFootballPlayerEntities(teamRequest.getFootballPlayerRequests(), idTeam);
        List<ResponseEntity<FootballPlayerResponse>> playerResponseEntities = null;

        List<FootballPlayerEntity> savedFootballPlayers = null;
        if (!footballPlayers.isEmpty()) {
            playerResponseEntities =
                    footballPlayers.stream().map(fp -> saveFootballPlayer(fp, idTeam)).collect(Collectors.toList());

            savedFootballPlayers = new ArrayList<>();
            for (ResponseEntity<FootballPlayerResponse> responseEntity : playerResponseEntities) {
                savedFootballPlayers.addAll(framePlayerEntities(responseEntity));
            }
        }

        return savedFootballPlayers;
    }

    private List<FootballPlayerEntity> framePlayerEntities(ResponseEntity<FootballPlayerResponse> playerResponse) {
        return playerResponse.getBody().getPlayerData().getFootballPlayerEntityList();
    }

    private void deletePlayersFromTeam(ResponseEntity<FootballTeamResponse> footballTeam) {

        List<FootballTeamEntity> teamEntities =
                Optional.ofNullable(
                        footballTeam.getBody().getTeamData().getFootballTeamEntities()).orElse(Collections.emptyList());

        for (FootballTeamEntity footballTeamEntity : teamEntities) {
            if (Objects.nonNull(footballTeamEntity.getFootballPlayerEntities())) {
                footballTeamEntity.getFootballPlayerEntities().forEach(
                        fp -> footballPlayerService.deletePlayerById(fp.getId())
                );
            }
        }
    }

    private List<FootballPlayerEntity> getFootballPlayers(FootballTeamEntity footballTeamEntity) {
        ResponseEntity<FootballPlayerResponse> footballPlayers =
                getFootballPlayersByTeam(footballTeamEntity.getId());

        if (HttpStatus.OK.equals(footballPlayers.getStatusCode()) && footballPlayers.hasBody()) {
            return footballPlayers.getBody().getPlayerData().getFootballPlayerEntityList();
        }
        return null;
    }

    private ResponseEntity<FootballPlayerResponse> getFootballPlayersByTeam(String idTeam) {
        return footballPlayerService.getPlayerByIdTeam(idTeam);
    }

    //Section for prop methods
    private FootballTeamEntity updatePropertiesTeamEntity(
            FootballTeamRequest teamRequest, FootballTeamEntity existingTeam) {

        existingTeam.setName(evaluateNull(teamRequest.getName(), existingTeam.getName()));
        existingTeam.setCountry(evaluateNull(teamRequest.getCountry(), existingTeam.getCountry()));
        existingTeam.setWins(evaluateNull(teamRequest.getWins(), existingTeam.getWins()));

        return existingTeam;
    }

    private List<FootballPlayerEntity> frameFootballPlayerEntities(
            List<FootballPlayerRequest> playerRequests,String idTeam) {

        if (Objects.nonNull(playerRequests)) {
            List<FootballPlayerEntity> footballPlayers = new ArrayList<>();
            for (FootballPlayerRequest footballPlayerRequest : playerRequests) {
                footballPlayers.add(
                        FootballPlayerEntity.builder()
                                .idTeam(idTeam)
                                .name(footballPlayerRequest.getName())
                                .age(footballPlayerRequest.getAge())
                                .rate(footballPlayerRequest.getRate())
                                .build()
                );
            }
            return  footballPlayers;
        }
        return Collections.emptyList();
    }

    private FootballTeamEntity frameFootballTeamEntity(FootballTeamRequest footballTeamRequest) {
        return FootballTeamEntity.builder()
                .name(footballTeamRequest.getName())
                .country(footballTeamRequest.getCountry())
                .wins(footballTeamRequest.getWins())
                .build();
    }

    private ResponseEntity<FootballTeamResponse> frameResponseEntity(HttpStatus httpStatus) {
        return frameResponseEntity(null, httpStatus);
    }

    private ResponseEntity<FootballTeamResponse> frameResponseEntity(
            List<FootballTeamEntity> footballTeamEntityList, HttpStatus httpStatus) {

        FootballTeamResponse footballTeamResponse = null;

        if (Objects.nonNull(footballTeamEntityList)) {
            footballTeamResponse = FootballTeamResponse.builder()
                    .teamData(FootballTeamResponse.TeamData.builder()
                            .footballTeamEntities(footballTeamEntityList)
                            .build())
                    .build();
        }
        return ResponseEntity.status(httpStatus).body(footballTeamResponse);
    }

    private <T> T evaluateNull(T value, T option) {
        return Optional.ofNullable(value).orElse(option);
    }
}
