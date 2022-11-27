package com.revisen.football.admin.controller;

import com.revisen.football.admin.request.FootballPlayerRequest;
import com.revisen.football.admin.response.FootballPlayerResponse;
import com.revisen.football.admin.service.FootballPlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class FootballPlayerController {

    @Autowired
    private final FootballPlayerService footballPlayerService;

    public FootballPlayerController(FootballPlayerService footballPlayerService) {
        this.footballPlayerService = footballPlayerService;
    }

    @PostMapping(value = "/football-player" ,produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballPlayerResponse> createPlayer(
            @RequestBody FootballPlayerRequest footballPlayerRequest) {
        return this.footballPlayerService.savePlayer(footballPlayerRequest);
    }

    @GetMapping(value = "/football-player", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballPlayerResponse> getPlayers(
            @RequestParam(value = "filter.idTeam",required = false) String idTeam) {
        return footballPlayerService.getPlayers(idTeam);
    }

    @GetMapping(value = "/football-player/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballPlayerResponse> getPlayer(
            @PathVariable String id) {
        return footballPlayerService.getPlayerById(id);
    }

    @PutMapping(value = "/football-player/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballPlayerResponse> updatePlayer(
            @PathVariable String id, @RequestBody FootballPlayerRequest footballPlayerRequest) {
        return footballPlayerService.updatePlayerById(footballPlayerRequest, id);
    }

    @DeleteMapping(value = "/football-player/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballPlayerResponse> deletePlayer(
            @PathVariable String id) {
        return footballPlayerService.deletePlayerById(id);
    }
}
