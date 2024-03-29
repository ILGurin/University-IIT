package com.bstu.UniversityIIT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {

    @GetMapping
    @PreAuthorize("hasAuthority('teacher:read')")
    public String get(){
        return "GET:: teacher controller";
    }

    @PostMapping
    @PreAuthorize("hasAuthority('teacher:read')'")
    public String post(){
        return "POST:: teacher controller";
    }
}
