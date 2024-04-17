package com.bstu.UniversityIIT.entity.DTO;

import lombok.Builder;

@Builder
public record PostDTO(
        Integer id,
        String title,
        String text,
        UserDTO userDTO,
        String creationDate
) {
}
