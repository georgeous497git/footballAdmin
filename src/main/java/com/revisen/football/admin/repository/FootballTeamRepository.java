package com.revisen.football.admin.repository;

import com.revisen.football.admin.entity.FootballTeamEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FootballTeamRepository extends MongoRepository<FootballTeamEntity, String> {

}
