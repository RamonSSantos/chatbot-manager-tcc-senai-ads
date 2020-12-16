package br.com.chatbot.backend.controllers;

import br.com.chatbot.backend.dtos.AuthenticationDto;
import br.com.chatbot.backend.dtos.AuthenticationRequestDto;
import br.com.chatbot.backend.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/auth")
    public ResponseEntity<AuthenticationDto> authenticate(@RequestBody AuthenticationRequestDto authenticationRequestDto, HttpServletResponse response) {
        AuthenticationDto authenticationDto = authenticationService.authenticate(authenticationRequestDto);

        // Create or set Cookie
        Cookie cookie = new Cookie("token", authenticationDto.getToken());
        response.addCookie(cookie);

        return ResponseEntity.ok(authenticationDto);
    }

    @GetMapping("/validate-token")
    public ResponseEntity<AuthenticationDto> validateToken(HttpServletRequest request, HttpServletResponse response) {

        String token = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        return ResponseEntity.ok().body(authenticationService.validateCookie(token));
    }

    @PostMapping("/do-logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

        String token = Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals("token"))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

        authenticationService.logout(token);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        return ResponseEntity.ok().body("Logout realizado com sucesso!");
    }
}
