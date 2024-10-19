package com.kdcd.ms_security.Controllers;


import com.kdcd.ms_security.Models.Role;
import com.kdcd.ms_security.Repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/roles")
public class RolesController {

    @Autowired//fabricas de objetos
    private RoleRepository theRoleRepository; //referencia a la clase de RoleRepository porque no se hace una intancia de una clase interfaz

    @GetMapping("")
    public List<Role> find(){
        return this.theRoleRepository.findAll();
    }

    @GetMapping("{id}")
    public Role findById(@PathVariable String id){
        Role theRole=this.theRoleRepository.findById(id).orElse(null);
        return theRole;
    }
    @PostMapping
    public Role create(@RequestBody Role newRole){
        return this.theRoleRepository.save(newRole);
    }
    @PutMapping("{id}")
    public Role update(@PathVariable String id, @RequestBody Role newRole){
        Role actualRole=this.theRoleRepository.findById(id).orElse(null);
        if(actualRole!=null){
            actualRole.setName(newRole.getName());
            actualRole.setDescription(newRole .getDescription());

            this.theRoleRepository.save(actualRole);
            return actualRole;
        }else{
            return null;
        }
    }



    @DeleteMapping("{id}")
    public void delete(@PathVariable String id){
        Role theRole=this.theRoleRepository.findById(id).orElse(null);
        if (theRole!=null){
            this.theRoleRepository.delete(theRole);
        }
    }

}


