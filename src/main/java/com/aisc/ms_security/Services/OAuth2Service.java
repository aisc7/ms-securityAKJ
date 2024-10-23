package com.aisc.ms_security.Services;

import com.aisc.ms_security.Models.User;
import com.aisc.ms_security.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2Service {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EncryptionService encryptionService;  // Inyectamos el servicio de encriptación

    @Value("${ms-notifications.base-url}")
    private String notificationServiceUrl; // URL del servicio de notificaciones

    @Value("${google.client.id}")
    private String googleClientId;

    @Value("${google.client.secret}")
    private String googleClientSecret;

    @Value("${google.redirect.uri}")
    private String googleRedirectUri;

    @Value("${google.auth.uri}")
    private String googleAuthUri;

    @Value("${google.token.uri}")
    private String googleTokenUri;

    @Value("${google.user.info}")
    private String googleUserInfo;


    // GitHub OAuth2
    @Value("${github.client.id}")
    private String githubClientId;

    @Value("${github.client.secret}")
    private String githubClientSecret;

    @Value("${github.redirect.uri}")
    private String githubRedirectUri;

    @Value("${github.auth.uri}")
    private String githubAuthUri;

    @Value("${github.token.uri}")
    private String githubTokenUri;

    @Value("${github.user.info}")
    private String githubUserInfo;

    private final RestTemplate resTemplate = new RestTemplate();

    // Método para obtener un usuario por su correo electrónico
    public Optional<User> getUserByEmail(String email) {
        return Optional.ofNullable(userRepository.getUserByEmail(email));
    }

    // Método para validar las credenciales del usuario
    public boolean validateUserCredentials(String email, String password) {
        User user = userRepository.getUserByEmail(email);
        if (user != null) {
            // Verificar la contraseña usando el método de cifrado
            String encryptedPassword = encryptionService.convertSHA256(password);
            return encryptedPassword.equals(user.getPassword());
        }
        return false;
    }

    public String getGoogleAuthUrl(String state) {
        //crea una url
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(googleAuthUri).
                queryParam("client_id", googleClientId).
                queryParam("redirect_uri", googleRedirectUri).
                queryParam("response_type", "code").
                queryParam("scope", "openid profile email").
                queryParam("state", state).queryParam("access_type", "offline").
                queryParam("prompt", "consent");
        return uriBuilder.toUriString();
    }


    public Map<String, Object> getGoogleUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Establece el token de acceso en el encabezado de autorización
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Realiza la solicitud a la API de información del usuario de Google
        ResponseEntity<Map> response = resTemplate.exchange(googleUserInfo, HttpMethod.GET, request, Map.class);

        return response.getBody(); // Retorna el cuerpo de la respuesta como un Map
    }

    // Métodos de GitHub OAuth2
    public String getGithubAuthUrl(String state) {
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(githubAuthUri)
                .queryParam("client_id", githubClientId)
                .queryParam("redirect_uri", githubRedirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "user") // Puedes ajustar los scopes según tus necesidades
                .queryParam("state", state);
        return uriBuilder.toUriString();
    }

    public Map<String, Object> getGoogleAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<Map> response = resTemplate.postForEntity(googleTokenUri, request, Map.class);
        return response.getBody();
    }


    public Map<String, Object> getGitHubAccessToken(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("client_id", githubClientId);
        params.add("client_secret", githubClientSecret);
        params.add("code", code);
        params.add("redirect_uri", githubRedirectUri); // Asegúrate de que esto sea el mismo URI que utilizaste para la autorización

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        // Realiza la solicitud POST para obtener el token
        ResponseEntity<Map> response = resTemplate.postForEntity(githubTokenUri, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody(); // Retorna el cuerpo de la respuesta como un Map
        } else {
            throw new RestClientException("Error al obtener el token: " + response.getStatusCode());
        }
    }

    public Map<String, Object> getGitHubUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken); // Establece el token de acceso en el encabezado de autorización
        HttpEntity<Void> request = new HttpEntity<>(headers);

        // Realiza la solicitud a la API de información del usuario de GitHub
        ResponseEntity<Map> response = resTemplate.exchange(githubUserInfo, HttpMethod.GET, request, Map.class);

        // Imprime la respuesta para depuración
        System.out.println("User Info Response: " + response.getBody());

        // Retorna el cuerpo de la respuesta como un Map
        return response.getBody();
    }

}



