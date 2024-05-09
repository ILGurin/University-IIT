package com.bstu.UniversityIIT.entity.DTO;

import com.bstu.UniversityIIT.entity.enums.Discipline;
import com.bstu.UniversityIIT.entity.enums.FileType;
import lombok.Builder;

@Builder
public record FileDTO(
    Integer id,
    long size,
    String httpContentType,
    String name,
    Discipline discipline,
    FileType fileType,
    UserDTO userDTO
){
}