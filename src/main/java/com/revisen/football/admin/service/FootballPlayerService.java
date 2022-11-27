package com.revisen.football.admin.service;

import com.revisen.football.admin.entity.FootballPlayerEntity;
import com.revisen.football.admin.repository.FootballPlayerRepository;
import com.revisen.football.admin.request.FootballPlayerRequest;
import com.revisen.football.admin.response.FootballPlayerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class FootballPlayerService {

    @Autowired
    private final FootballPlayerRepository footballPlayerRepository;

    public FootballPlayerService(FootballPlayerRepository footballPlayerRepository) {
        this.footballPlayerRepository = footballPlayerRepository;
    }

    public ResponseEntity<FootballPlayerResponse> getPlayers(String idTeam) {
        if (Objects.isNull(idTeam)) {
            log.info("Fetching All Players");
            return getAllPlayers();
        }

        log.info("Fetching Player by Id");
        return getPlayerByIdTeam(idTeam);
    }

    public ResponseEntity<FootballPlayerResponse> getAllPlayers() {
        return frameResponseEntity(footballPlayerRepository.findAll(), HttpStatus.OK);
    }

    public ResponseEntity<FootballPlayerResponse> getPlayerById(String id) {
        Optional<FootballPlayerEntity> optional = footballPlayerRepository.findById(id);

        if (optional.isPresent()) {
            FootballPlayerEntity footballPlayerEntity = optional.get();
            return frameResponseEntity(Collections.singletonList(footballPlayerEntity), HttpStatus.OK);
        }

        return frameResponseEntity(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<FootballPlayerResponse> getPlayerByIdTeam(String idTeam) {
        List<FootballPlayerEntity> footballPlayers = footballPlayerRepository.findByIdTeam(idTeam);

        if (Objects.nonNull(footballPlayers) && !footballPlayers.isEmpty()) {
            return frameResponseEntity(footballPlayers, HttpStatus.OK);
        }

        return frameResponseEntity(Collections.emptyList(), HttpStatus.NOT_FOUND);
    }

    public ResponseEntity<FootballPlayerResponse> savePlayer(FootballPlayerRequest playerRequest) {
        FootballPlayerEntity footballPlayerEntity =
                frameFootballPlayerEntity(playerRequest);

        return savePlayer(footballPlayerEntity);
    }

    public ResponseEntity<FootballPlayerResponse> savePlayer(FootballPlayerEntity playerEntity) {
        log.info("Saving Player Entity");
        FootballPlayerEntity savedPlayer = footballPlayerRepository.save(playerEntity);

        return frameResponseEntity(Collections.singletonList(savedPlayer), HttpStatus.CREATED);
    }

    public ResponseEntity<FootballPlayerResponse> deletePlayerById(String id) {
        log.info("Deleting Player");
        ResponseEntity<FootballPlayerResponse> player = getPlayerById(id);

        if (player.hasBody() && HttpStatus.OK.equals(player.getStatusCode())) {
            footballPlayerRepository.deleteById(id);
            return frameResponseEntity(HttpStatus.OK);
        }
        return frameResponseEntity(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<FootballPlayerResponse> updatePlayerById(
            FootballPlayerRequest playerRequest, String id) {
        log.info("Updating Player");
        ResponseEntity<FootballPlayerResponse> player = getPlayerById(id);

        if (player.hasBody() && HttpStatus.OK.equals(player.getStatusCode())) {
            FootballPlayerEntity existingPlayerEntity =
                    player.getBody().getPlayerData().getFootballPlayerEntityList().get(0);

            FootballPlayerEntity savedPlayer = footballPlayerRepository.save(
                    updatePropertiesFootballPlayerEntity(playerRequest, existingPlayerEntity));

            return frameResponseEntity(Collections.singletonList(savedPlayer), HttpStatus.OK);
        }
        return frameResponseEntity(HttpStatus.NO_CONTENT);
    }

    private FootballPlayerEntity updatePropertiesFootballPlayerEntity(
            FootballPlayerRequest playerRequest, FootballPlayerEntity existingPlayerEntity) {

        return FootballPlayerEntity.builder()
                .id(existingPlayerEntity.getId())
                .name(evaluateNull(playerRequest.getName(), existingPlayerEntity.getName()))
                .rate(evaluateNull(playerRequest.getRate(), existingPlayerEntity.getRate()))
                .age(evaluateNull(playerRequest.getAge(), existingPlayerEntity.getAge()))
                .idTeam(evaluateNull(playerRequest.getIdTeam(), existingPlayerEntity.getIdTeam()))
                .build();
    }

    private <T> T evaluateNull(T value, T option) {
        return Optional.ofNullable(value).orElse(option);
    }

    private FootballPlayerEntity frameFootballPlayerEntity(FootballPlayerRequest playerRequest) {
        return FootballPlayerEntity.builder()
                .name(playerRequest.getName())
                .age(playerRequest.getAge())
                .rate(playerRequest.getRate())
                .idTeam(playerRequest.getIdTeam())
                .build();
    }

    private ResponseEntity<FootballPlayerResponse> frameResponseEntity(HttpStatus httpStatus) {
        return frameResponseEntity(null, httpStatus);
    }

    private ResponseEntity<FootballPlayerResponse> frameResponseEntity(
            List<FootballPlayerEntity> playerEntities, HttpStatus httpStatus) {

        FootballPlayerResponse playerResponse = null;

        if (Objects.nonNull(playerEntities)) {
            FootballPlayerResponse.PlayerData playerData = FootballPlayerResponse.PlayerData.builder()
                    .footballPlayerEntityList(playerEntities).build();

            playerResponse = FootballPlayerResponse.builder()
                    .playerData(playerData)
                    .build();
        }
        return ResponseEntity.status(httpStatus).body(playerResponse);
    }
}
