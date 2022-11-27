package com.revisen.football.admin.repository;

import com.revisen.football.admin.entity.FootballPlayerEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FootballPlayerRepository extends MongoRepository<FootballPlayerEntity, String> {

    List<FootballPlayerEntity> findByIdTeam(String idTeam);
}
