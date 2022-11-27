package com.revisen.football.admin.controller;

import com.revisen.football.admin.request.FootballTeamRequest;
import com.revisen.football.admin.response.FootballTeamResponse;
import com.revisen.football.admin.service.FootballTeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/v1")
public class FootballTeamController {

    @Autowired
    private final FootballTeamService footballTeamService;

    public FootballTeamController(FootballTeamService footballTeamService) {
        this.footballTeamService = footballTeamService;
    }

    @PostMapping(value = "/football-team", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> createTeam(@RequestBody FootballTeamRequest footballTeamRequest) {
        return footballTeamService.saveTeam(footballTeamRequest);
    }

    @GetMapping(value = "/football-team", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> getTeams() {
        return footballTeamService.getTeams();
    }

    @GetMapping(value = "/football-team/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> getTeamsById(@PathVariable String id) {
        return footballTeamService.getTeamById(id);
    }

    @DeleteMapping(value = "/football-team/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> deleteTeamById(@PathVariable String id) {
        return footballTeamService.deleteTeamById(id);
    }

    @PutMapping(value = "/football-team/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> updateTeamById(
            @PathVariable String id, @RequestBody FootballTeamRequest footballTeamRequest) {
        return footballTeamService.completeUpdateTeamById(id, footballTeamRequest);
    }

    @PatchMapping(value = "/football-team/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<FootballTeamResponse> partialUpdateTeamById(
            @PathVariable String id, @RequestBody FootballTeamRequest footballTeamRequest) {
        return footballTeamService.partialUpdateTeamById(id, footballTeamRequest);
    }
}
