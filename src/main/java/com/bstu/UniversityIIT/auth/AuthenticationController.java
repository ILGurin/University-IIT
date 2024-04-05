package com.bstu.UniversityIIT.auth;

import com.bstu.UniversityIIT.entity.UserDTO;
import com.bstu.UniversityIIT.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")   //TODO admin controller
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ){
        var resp = authenticationService.register(request);
        if(resp == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        return ResponseEntity.ok(resp);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody AuthenticationRequest request,
            HttpServletResponse response
    ){
        var result = authenticationService.authenticate(request);

        // <---   cors problem   --->
        /*Cookie cookie = new Cookie("refresh_token", result.getRefreshToken());
        //cookie.setPath("/api/v1/auth");
        cookie.setPath("http://localhost:8080/api/v1/auth/refresh-token");
        cookie.setMaxAge(86400);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);*/
        //return ResponseEntity.ok(authenticationService.authenticate(request));

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/refresh-token")
    public void refreshToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @PostMapping("/check-auth")
    public void checkAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        //bearer token
    }

    @PostMapping("/get-user")
    public void getCurrentUser(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.getCurrentUser(request, response);
    }

}

