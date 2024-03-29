package com.bstu.UniversityIIT.entity;

import lombok.Builder;

@Builder
public record UserDTO(
        Integer id,
        String username,
        Role role
) {
}
