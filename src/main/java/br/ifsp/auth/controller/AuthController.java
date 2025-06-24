package br.ifsp.auth.controller;

import br.ifsp.auth.dto.AuthenticationDTO;
import br.ifsp.auth.service.AuthenticationService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    public String authenticate(@RequestBody AuthenticationDTO request) {
        UsernamePasswordAuthenticationToken auth = 
            new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());
        return authenticationService.authenticate(auth);
    }
}
