package com.aisc.ms_security.Repositories;
import com.aisc.ms_security.Models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role,String> {
    Optional<Role> findByName(String name);
}
