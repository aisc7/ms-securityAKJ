package com.aisc.ms_security.Repositories;

import com.aisc.ms_security.Models.RolePermission;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface RolePermissionRepository extends MongoRepository<RolePermission, String> {
    @Query("{'role.$id': ObjectId(?0)}")
    List<RolePermission> getPermissionsByRole(String roleId);

    @Query("{'role.$id': ObjectId(?0),'permission.$id': ObjectId(?1)}")
    RolePermission getRolePermission(String roleId, String permissionId);
}
