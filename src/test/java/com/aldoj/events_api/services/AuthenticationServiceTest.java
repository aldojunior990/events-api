package com.aldoj.events_api.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import com.aldoj.events_api.configs.TokenService;
import com.aldoj.events_api.domain.users.User;
import com.aldoj.events_api.dtos.AuthenticationResponseDTO;
import com.aldoj.events_api.dtos.SignInDTO;
import com.aldoj.events_api.dtos.SignUpDTO;
import com.aldoj.events_api.dtos.UserDTO;
import com.aldoj.events_api.models.AuthenticationErrorMessage;
import com.aldoj.events_api.repositories.UserRepository;

@ExtendWith(MockitoExtension.class)
public class AuthenticationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TokenService tokenService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private AuthenticationService authenticationService;

    private SignInDTO validAuthenticationDTO;
    private User validUser;

    private User invalidUsersCredentials;

    @BeforeEach
    public void setUp() {
        this.validAuthenticationDTO = new SignInDTO("aldo@gmail.com", "123456");
        this.validUser = new User(new UserDTO("aldo", "aldo@gmail.com", "123456"));
        this.invalidUsersCredentials = new User(new UserDTO("aldo", "", "00000"));
    }

    @Test
    public void user_logs_in_with_correct_token_return() {
        // Verifica se retornou 200 OK
        // Verfica se o token foi retornado para o usuario

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(this.validUser);
        when(authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(this.validUser.getEmail(),
                        this.validUser.getPassword())))
                .thenReturn(authentication);
        when(this.tokenService.generateToken((User) authentication.getPrincipal()))
                .thenReturn("token");

        var response = this.authenticationService.signIn(validAuthenticationDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("token", response.getBody().message());
    }

    @Test
    public void users_tries_to_log_in_but_their_credentials_are_invalid() {

        // Verifica se retornou 400 BAD REQUEST
        // Verifica se retornou a mensagem "Invalid credentials"

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(this.invalidUsersCredentials);
        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(this.invalidUsersCredentials.getEmail(),
                        this.invalidUsersCredentials.getPassword())))
                .thenReturn(authentication);
        when(this.tokenService.generateToken((User) authentication.getPrincipal()))
                .thenReturn("token");

        var response = this.authenticationService.signIn(validAuthenticationDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Invalid Credentials", response.getBody().message());

    }

    @Test
    public void signUp_New_User_Success() {

        SignUpDTO signUpDTO = new SignUpDTO("username", "user@example.com", "password123");

        when(userRepository.findByEmail(signUpDTO.email())).thenReturn(null);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationService.signUp(signUpDTO);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void signUp_User_Already_Registered_BadRequest() {

        SignUpDTO signUpDTO = new SignUpDTO("username", "user@example.com", "password123");

        User existingUser = new User(new UserDTO("username", "user@example.com", "password123"));
        when(userRepository.findByEmail(signUpDTO.email())).thenReturn(existingUser);

        ResponseEntity<AuthenticationResponseDTO> response = authenticationService.signUp(signUpDTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(AuthenticationErrorMessage.USER_ALREADY_REGISTERED.getMessage(), response.getBody().message());
    }

}
