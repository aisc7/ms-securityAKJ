package com.aisc.ms_security.Repositories;

import com.aisc.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionRepository extends MongoRepository<Session, String> {
    @Query("{'user._id': ?0, 'token': ?1}")
    Session getSessionByUserId(String userId, int token);

    @Query("{'user._id': ?0}")
    public List<Session> getSessionsByUserId(String userId);
}
