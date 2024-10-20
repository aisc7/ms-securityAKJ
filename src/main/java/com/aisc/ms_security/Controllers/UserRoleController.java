package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.Role;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Models.UserRole;
import com.aisc.ms_security.Repositories.RoleRepository;
import com.aisc.ms_security.Repositories.UserRepository;
import com.aisc.ms_security.Repositories.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

///Se ponen los tres reposiotrios para acceder a user a rol y a userrole porque se interactua con las tres clases
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/user_role")

public class UserRoleController {

    @Autowired
    UserRepository theUserRepository;

    @Autowired
    RoleRepository theRoleRepository;

    @Autowired
    UserRoleRepository theUserRoleRepository;


    @PostMapping("user/{userId}/role/{roleId}")

    //aca se leen los dos parametros que vienen por la ruta
    public UserRole create(@PathVariable String userId, @PathVariable String roleId) {
        User theuser = this.theUserRepository.findById(userId).orElse(null);
        Role therole = this.theRoleRepository.findById(roleId).orElse(null);
        if(theuser != null && therole != null) {
            UserRole newUserRole = new UserRole();
            //Enlaces de la relación N-N
            newUserRole.setUser(theuser);
            newUserRole.setRole(therole);
            this.theUserRoleRepository.save(newUserRole);
            return newUserRole;
        }else{
            return null;
        }
    }


    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        User theUser= this.theUserRepository.findById(id).orElse(null);
        if(theUser != null){
            this.theUserRepository.delete(theUser);
        }
    }

    @GetMapping("user/{userId}")
    public List<UserRole> getRolesByUser(@PathVariable String userId){
        return this.theUserRoleRepository.getRolesByUser(userId);
    }

    //Devuelve los usuarios por un role
    @GetMapping("role/{roleId}")
    public List<UserRole> getUsersByRole(@PathVariable String roleId){
        return this.theUserRoleRepository.getUsersByRole(roleId);
    }


    }
