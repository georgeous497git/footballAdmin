package com.revisen.football.admin.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FootballPlayerRequest {
    private String name;
    private Integer age;
    private Double rate;
    private String idTeam;
}
