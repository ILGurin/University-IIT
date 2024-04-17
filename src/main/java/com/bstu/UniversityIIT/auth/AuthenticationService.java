package com.bstu.UniversityIIT.auth;

import com.bstu.UniversityIIT.config.JwtService;
import com.bstu.UniversityIIT.entity.*;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import com.bstu.UniversityIIT.repository.TokenRepository;
import com.bstu.UniversityIIT.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .tokens(null) //Can be removed
                .build();

        var userFromDb = repository.findByUsername(user.getUsername()); //Throw an exception
        if(userFromDb.isPresent()){
            return null;
        }
        var savedUser = repository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(savedUser, jwtToken);

        var userDTO = UserDTO.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .build();
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userDTO(userDTO)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = repository.findByUsername(request.getUsername())
                .orElseThrow();
        var userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
        var jwtToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .refreshToken(refreshToken)
                .userDTO(userDTO)
                .build();
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public void getCurrentUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        final String accessToken;
        final String username;

        accessToken = authHeader.substring(7);
        username = jwtService.extractUsername(accessToken);

        if(username != null){
            var user = this.repository.findByUsername(username)
                    .orElseThrow();
            var userDTO = UserDTO.builder()
                    .id(user.getId())
                    .username(user.getUsername())
                    .role(user.getRole())
                    .build();
            if(jwtService.isTokenValid(accessToken, user)){
                var authResponse = AuthenticationResponse.builder()
                        .accessToken(accessToken)
                        .refreshToken("active")
                        .userDTO(userDTO)
                        .build();
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }else{
                var authResponse = AuthenticationResponse.builder()
                        .accessToken("expired")
                        .build();
                response.setContentType("application/json");
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    public ResponseEntity<?> refreshToken(String refreshToken) {
        String username = jwtService.extractUsername(refreshToken);
        User user = repository.findByUsername(username).orElseThrow();
        var tokens = tokenRepository.findAllValidTokensByUser(user.getId());
        for (Token token : tokens){
            if(Objects.equals(token.getToken(), refreshToken)){
                return new ResponseEntity<>("Нужно прислать refreshToken а не accessToken", HttpStatus.BAD_REQUEST);
            }
        }
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .build();
        var newAccessToken = jwtService.generateToken(user);
        var newRefreshToken = jwtService.generateRefreshToken(user);
        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .userDTO(userDTO)
                .build();

        revokeAllUserTokens(user);
        saveUserToken(user, newAccessToken);
        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }
}
