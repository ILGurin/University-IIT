package com.bstu.UniversityIIT.entity.DTO;

import com.bstu.UniversityIIT.entity.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        Integer id,
        String username,
        Role role
) {
}
