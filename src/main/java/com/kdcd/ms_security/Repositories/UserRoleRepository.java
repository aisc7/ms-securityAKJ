package com.kdcd.ms_security.Repositories;

import com.kdcd.ms_security.Models.Session;
import com.kdcd.ms_security.Models.UserRole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface UserRoleRepository extends MongoRepository<UserRole, String> {
    //consulta de mongo
    @Query("{'user.$id': ObjectId(?0)}")
    //lista todos los rols que tiene un usuario
    public List<UserRole> getRolesByUser(String userId);

    //filtra por role
    @Query("{'role.$id': ObjectId(?0)}")
    public List<UserRole> getUsersByRole(String roleId);


}
