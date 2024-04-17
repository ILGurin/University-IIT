package com.bstu.UniversityIIT.service.mapper;

import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class UserDTOMapper implements Function<User, UserDTO> {

    @Override
    public UserDTO apply(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}
