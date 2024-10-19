package com.kdcd.ms_security.Interceptors;

import com.kdcd.ms_security.Services.ValidatorsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


//Esta clase define como se van a activar las cosas, se esta haciendo la implementacion de una interfaz(HandlerInterceptor)
@Component
//Configura el comportamiento del policia
public class SecurityInterceptor implements HandlerInterceptor {


//El validatorService es el policia que tiene todo el analisis del código
    @Autowired
    private ValidatorsService validatorService;

    //Sobreescritura de los metodos de la interfaz
    @Override
    //Aca se define de que antes de que  entre al a (colocar policia)
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler)
            throws Exception {
        //Decide si entra, si la respuesta es true entra sino false, se obtiene la url y el metodo del endpoint
        boolean success = this.validatorService.validationRolePermission(request,request.getRequestURI(),request.getMethod());
        return success;
    }

    //pone el policia a la salida
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
        // Lógica a ejecutar después de que se haya manejado la solicitud por el controlador
    }

    //se deja sin nada porque no es relevante
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                Exception ex) throws Exception {
        // Lógica a ejecutar después de completar la solicitud, incluso después de la renderización de la vista
    }
}
