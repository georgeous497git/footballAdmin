package com.revisen.football.admin.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.List;

@Data
@Builder
@Document("footballteam")
public class FootballTeamEntity {
    @MongoId
    private String id;
    private String name;
    private String country;
    private Integer wins;

    @JsonProperty("football-players")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FootballPlayerEntity> footballPlayerEntities;
}
