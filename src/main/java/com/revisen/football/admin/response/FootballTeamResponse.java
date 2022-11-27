package com.revisen.football.admin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revisen.football.admin.entity.FootballTeamEntity;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FootballTeamResponse {

    @JsonProperty("data")
    private TeamData teamData;

    @lombok.Data
    @Builder
    public static class TeamData {
        @JsonProperty("football-teams")
        private List<FootballTeamEntity> footballTeamEntities;
    }
}
