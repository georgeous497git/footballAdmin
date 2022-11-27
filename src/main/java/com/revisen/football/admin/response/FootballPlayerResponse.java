package com.revisen.football.admin.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.revisen.football.admin.entity.FootballPlayerEntity;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class FootballPlayerResponse {

    @JsonProperty("data")
    private PlayerData playerData;

    @lombok.Data
    @Builder
    public static class PlayerData {
        @JsonProperty("football-players")
        private List<FootballPlayerEntity> footballPlayerEntityList;
    }
}
