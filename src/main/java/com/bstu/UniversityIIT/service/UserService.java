package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.UserDTO;
import com.bstu.UniversityIIT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()
                )).collect(Collectors.toList());
    }

    public UserDTO getUser(Integer id){
        return userRepository.findById(id)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getRole()
                )).orElseThrow();
    }
}
