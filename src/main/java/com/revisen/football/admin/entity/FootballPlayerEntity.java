package com.revisen.football.admin.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Data
@Builder
@Document(collection = "footballplayer")
public class FootballPlayerEntity {
    @MongoId
    private String id;
    private String name;
    private Integer age;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double rate;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String idTeam;
}
