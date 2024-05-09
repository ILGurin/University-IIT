package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.DTO.FileDTO;
import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.service.mapper.FileDTOMapper;
import com.bstu.UniversityIIT.entity.enums.Discipline;
import com.bstu.UniversityIIT.entity.FileMetadataEntity;
import com.bstu.UniversityIIT.repository.FileMetadataRepository;
import com.bstu.UniversityIIT.entity.enums.FileType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final String SCHEDULE_PATH = System.getProperty("user.home") + "/Downloads/IIT_test_files/schedule";
    private final String SCHEDULE_FEIS_PATH = System.getProperty("user.home") + "/Downloads/IIT_test_files/scheduleFeis";
    private final String MAIN_PATH = System.getProperty("user.home") + "/Downloads/IIT_test_files";
    private final UserService userService;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileDTOMapper fileDTOMapper;

    public String uploadFile(MultipartFile file, String type){
        return fileFunction.apply(file, type);
    }

    private final BiFunction<MultipartFile, String, String> fileFunction = (file, type) -> {
        Path fileStorageLocation = null;
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case "iit" -> fileStorageLocation = Paths.get(SCHEDULE_PATH).toAbsolutePath().normalize();
            case "feis1" -> fileStorageLocation = Paths.get(SCHEDULE_FEIS_PATH).toAbsolutePath().normalize();
            case "feis2" -> fileStorageLocation = Paths.get(SCHEDULE_FEIS_PATH).toAbsolutePath().normalize();
            case "feis3" -> fileStorageLocation = Paths.get(SCHEDULE_FEIS_PATH).toAbsolutePath().normalize();
            case "feis4" -> fileStorageLocation = Paths.get(SCHEDULE_FEIS_PATH).toAbsolutePath().normalize();
        }
        switch (type) {
            case "iit" -> sb.append("schedule.pdf");
            case "feis1" -> sb.append("schedule1.pdf");
            case "feis2" -> sb.append("schedule2.pdf");
            case "feis3" -> sb.append("schedule3.pdf");
            case "feis4" -> sb.append("schedule4.pdf");
        }

        try{
            if(!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(sb.toString()), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/schedule/" + type).toUriString();
        } catch (Exception e){
            throw new RuntimeException("Exception");
        }
    };

    public String upload(MultipartFile file, FileType fileType, Discipline discipline, UserDetails user){
        String fileStorageLocationString = MAIN_PATH + "/" + fileType.toString() + "/" + user.getUsername();
        Path fileStorageLocation = Paths.get(fileStorageLocationString).toAbsolutePath().normalize();

        if(!Files.exists(fileStorageLocation)){
            try {
                Files.createDirectories(fileStorageLocation);
            } catch (IOException e) {
                throw new RuntimeException("Error when creating a directory: ", e);
            }
        }

        try {
            Files.copy(file.getInputStream(), fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename())), REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Error when saving a file", e);
        }

        var fileFromDb = fileMetadataRepository.findFileMetadataEntityByFilePath(
                fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename())).toString()
        );
        if(fileFromDb.isPresent()){
            FileMetadataEntity fileEntity = fileFromDb.orElseThrow();
            fileEntity.setSize(file.getSize());
            fileEntity.setHttpContentType(file.getContentType());
            fileEntity.setName(file.getOriginalFilename());
            fileEntity.setFilePath(fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename())).toString());
            fileEntity.setDiscipline(discipline);
            fileEntity.setFileType(fileType);
            fileEntity.setUser(userService.findByUsername(user.getUsername()));
            fileMetadataRepository.save(fileEntity);
        } else {
            FileMetadataEntity fileMetadataEntity = FileMetadataEntity.builder()
                    .size(file.getSize())
                    .httpContentType(file.getContentType())
                    .name(file.getOriginalFilename())
                    .filePath(fileStorageLocation.resolve(Objects.requireNonNull(file.getOriginalFilename())).toString())
                    .discipline(discipline)
                    .fileType(fileType)
                    .user(userService.findByUsername(user.getUsername()))
                    .build();
            fileMetadataRepository.save(fileMetadataEntity);
        }

        //System.out.println(fileMetadataEntity.getId());
        return ServletUriComponentsBuilder.fromCurrentContextPath().path("api/v1/").toUriString();
    }




    public List<FileDTO> getAllMaterials(){
        return fileMetadataRepository.findAllMaterials()
                .stream()
                .map(fileDTOMapper)
                .toList();
    }

    public List<FileDTO> getAllExamQuestions(){
        return fileMetadataRepository.findAllExamQuestions()
                .stream()
                .map(fileDTOMapper)
                .toList();
    }

    public List<FileDTO> getAllLabWorks(){
        return fileMetadataRepository.findAllLabs()
                .stream()
                .map(fileDTOMapper)
                .toList();
    }

    public FileDTO getFileById(Integer id){
        return fileMetadataRepository.findFileMetadataEntityById(id)
                .map(fileDTOMapper)
                .orElseThrow();
    }

    public List<FileDTO> getAllMaterialsByUser(User user){
        return fileMetadataRepository.findAllMaterialsByUser(user)
                .stream()
                .map(fileDTOMapper)
                .toList();
    }

    public List<FileDTO> getALlExamQuestionsByUser(User user) {
        return fileMetadataRepository.findAllExamQuestionsByUser(user)
                .stream()
                .map(fileDTOMapper)
                .collect(Collectors.toList());
    }

    public List<FileDTO> getAllLabWorksByUser(User user){
        return fileMetadataRepository.findAllLabsByUser(user)
                .stream()
                .map(fileDTOMapper)
                .collect(Collectors.toList());
    }
}
