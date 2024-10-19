package com.kdcd.ms_security.Repositories;

import com.kdcd.ms_security.Models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

//interface donde vamos a tener un contrato de métodos
public interface UserRepository extends MongoRepository<User, String>{
@Query("{'email': ?0}")
    public User getUserByEmail(String email);
}
