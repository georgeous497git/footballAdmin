package com.revisen.football.admin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class FootballTeamRequest {
    private String name;
    private String country;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer wins;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("football-players")
    List<FootballPlayerRequest> footballPlayerRequests;
}
