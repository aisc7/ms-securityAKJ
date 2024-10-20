package com.aisc.ms_security.Repositories;

import com.aisc.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


//Es la que ayuda a gestionar todos los datos
public interface SessionRepository  extends MongoRepository<Session, String>{
    @Query("{'theUser.$id': ObjectId(?0),'token': ?1}")
    Session getSessionbyUserId(String userId, int token);
    public List<Session>getSessionsByUserId(String userId);
}
