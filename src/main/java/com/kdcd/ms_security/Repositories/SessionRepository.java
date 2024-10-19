package com.kdcd.ms_security.Repositories;

import com.kdcd.ms_security.Models.Session;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;


//Es la que ayuda a gestionar todos los datos
public interface SessionRepository extends MongoRepository<Session, String> {
    //consulta de mongo
    @Query("{'user.$id': ObjectId(?0)}")
    public List<Session>getSessionsByUserId(String userId);
}
