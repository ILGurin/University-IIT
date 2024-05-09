package com.bstu.UniversityIIT.service.mapper;

import com.bstu.UniversityIIT.entity.DTO.FileDTO;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import com.bstu.UniversityIIT.entity.FileMetadataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class FileDTOMapper implements Function<FileMetadataEntity, FileDTO> {
    private final UserDTOMapper userDTOMapper;

    @Override
    public FileDTO apply(FileMetadataEntity fileMetadataEntity) {
        UserDTO userDTO = userDTOMapper.apply(fileMetadataEntity.getUser());

        return new FileDTO(
                fileMetadataEntity.getId(),
                fileMetadataEntity.getSize(),
                fileMetadataEntity.getHttpContentType(),
                fileMetadataEntity.getName(),
                fileMetadataEntity.getDiscipline(),
                fileMetadataEntity.getFileType(),
                userDTO
        );
    }
}
