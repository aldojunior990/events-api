package com.aldoj.events_api.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.aldoj.events_api.configs.TokenService;
import com.aldoj.events_api.domain.users.User;
import com.aldoj.events_api.dtos.SignInDTO;
import com.aldoj.events_api.dtos.SignUpDTO;
import com.aldoj.events_api.dtos.UserDTO;
import com.aldoj.events_api.models.AuthenticationErrorMessage;
import com.aldoj.events_api.dtos.AuthenticationResponseDTO;
import com.aldoj.events_api.repositories.UserRepository;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    public ResponseEntity<AuthenticationResponseDTO> signIn(SignInDTO data) {
        try {
            var authenticate = new UsernamePasswordAuthenticationToken(data.email(), data.password());

            var auth = this.authenticationManager.authenticate(authenticate);

            var token = tokenService.generateToken((User) auth.getPrincipal());

            return ResponseEntity.ok(new AuthenticationResponseDTO(token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthenticationResponseDTO("Invalid Credentials"));
        }
    }

    public ResponseEntity<AuthenticationResponseDTO> signUp(SignUpDTO data) {
        try {
            if (this.repository.findByEmail(data.email()) != null)
                return ResponseEntity.badRequest().body(
                        new AuthenticationResponseDTO(AuthenticationErrorMessage.USER_ALREADY_REGISTERED.getMessage()));

            var encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
            var user = new User(new UserDTO(data.username(), data.email(), encryptedPassword));
            repository.save(user);
            return ResponseEntity.ok().build();
        } catch (Exception e) {

            return ResponseEntity.badRequest().body(new AuthenticationResponseDTO(e.getMessage()));
        }

    }
}
