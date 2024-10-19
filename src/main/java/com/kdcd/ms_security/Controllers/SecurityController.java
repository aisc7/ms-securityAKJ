package com.kdcd.ms_security.Controllers;

import com.kdcd.ms_security.Models.User;
import com.kdcd.ms_security.Repositories.UserRepository;
import com.kdcd.ms_security.Services.EncryptionService;
import com.kdcd.ms_security.Services.JwtService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;
import java.util.HashMap;

@CrossOrigin
@RestController
//El archivo es publico
@RequestMapping("/api/public/security")
public class SecurityController {
    @Autowired
    private UserRepository theUserRepository;
    @Autowired
    private EncryptionService theEncryptionService;
    @Autowired
    private JwtService theJwtService;

    @PostMapping("/login")
    public HashMap<String,Object> login(@RequestBody User theNewUser,
                                        final HttpServletResponse response)throws IOException {
        HashMap<String,Object> theResponse=new HashMap<>();
        String token="";
        //Se verifica si el email ingresado existe en el repositorio
        User theActualUser=this.theUserRepository.getUserByEmail(theNewUser.getEmail());
        //Se verifica si el email ingresado si esta y si tambien la contraseña ingresada es igual a la contraseña del usuario
        if(theActualUser!=null &&
           theActualUser.getPassword().equals(theEncryptionService.convertSHA256(theNewUser.getPassword()))){
            //Se genera el token de inicio de sesion
            token=theJwtService.generateToken(theActualUser);
            theActualUser.setPassword("");
            theResponse.put("token",token);
            theResponse.put("user",theActualUser);
            return theResponse;
        }else{
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return  theResponse;
        }

        //hay que validar que al crear no deben haber dos personas con  el mismo correo

    }

}
