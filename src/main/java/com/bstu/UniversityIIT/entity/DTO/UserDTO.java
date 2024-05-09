package com.bstu.UniversityIIT.entity.DTO;

import com.bstu.UniversityIIT.entity.enums.Role;
import lombok.Builder;

@Builder
public record UserDTO(
        Integer id,
        String username,
        Role role
) {
}
