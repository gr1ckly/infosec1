package ru.itmo.secureapi.auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import ru.itmo.secureapi.auth.dto.LoginRequest;
import ru.itmo.secureapi.auth.dto.LoginResponse;
import ru.itmo.secureapi.security.AppUserPrincipal;
import ru.itmo.secureapi.security.JwtService;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        var authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.username(), request.password()));
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof AppUserPrincipal appUserPrincipal)) {
            throw new AuthenticationServiceException("Unsupported authenticated principal");
        }
        return jwtService.issue(appUserPrincipal);
    }
}
