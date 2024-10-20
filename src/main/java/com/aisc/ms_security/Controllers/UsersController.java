package com.aisc.ms_security.Controllers;

import com.aisc.ms_security.Models.Role;
import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.RoleRepository;
import com.aisc.ms_security.Repositories.UserRepository;
import com.aisc.ms_security.Services.EncryptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
//Se pone api para decir que todoslos enpoints van a ser protegidos, no cuaquiera puede ver la informacion de lo usuarios
@RequestMapping ("/users")

public class UsersController {

    @Autowired
    UserRepository theUserRepository;

    //Aqui se esta inyectando
    @Autowired
    EncryptionService theEncryptionService;


    //Construccion Apis
    @GetMapping("")
    public List<User> find(){
        return this.theUserRepository.findAll();
    }

    @GetMapping("{id}")
    public User findById(@PathVariable String id){
        User theUser= this.theUserRepository.findById(id).orElse(null);
        return theUser;
    }

    @PostMapping
    public User create(@RequestBody User newUser){
        newUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
        return this.theUserRepository.save(newUser);

    }

    //Actualiza un usuario
    @PutMapping("{id}")
    public User update(@PathVariable String id, @RequestBody User newUser){
        User actualUser = this.theUserRepository.findById(id).orElse(null);
        if(actualUser!=null){
            //el identiificador nunca se actualiza
            actualUser.setName(newUser.getName());
            actualUser.setEmail(newUser.getEmail());
            actualUser.setPassword(this.theEncryptionService.convertSHA256(newUser.getPassword()));
            this.theUserRepository.save(actualUser);
            return actualUser;
        }
        else {
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
}
