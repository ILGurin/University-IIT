package com.bstu.UniversityIIT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teacher")
@PreAuthorize("hasRole('TEACHER')")
@CrossOrigin(value = "http://localhost:5173", allowCredentials = "true") //Не учитываются, если включен spring security?
public class TeacherController {

    @GetMapping
    @PreAuthorize("hasAuthority('teacher:read')")
    public ResponseEntity<String> get(){
        return new ResponseEntity<String>("GET:: teacher controller", HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('teacher:read')'")
    public String post(){
        return "POST:: teacher controller";
    }
}
