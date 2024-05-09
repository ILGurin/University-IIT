package com.bstu.UniversityIIT.controller;

import com.bstu.UniversityIIT.entity.DTO.FileDTO;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.service.FileService;
import com.bstu.UniversityIIT.service.UserService;
import com.bstu.UniversityIIT.service.mapper.FileDTOMapper;
import com.bstu.UniversityIIT.entity.enums.Discipline;
import com.bstu.UniversityIIT.repository.FileMetadataRepository;
import com.bstu.UniversityIIT.entity.enums.FileType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Tag(
        name = "Публичный контроллер",
        description = "Доступ к этому репозиторию могут получить любые пользователи (в т ч и без авторизации)"
)
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PublicController {
    public final UserService userService;
    private final FileService fileService;
    private final FileMetadataRepository fileMetadataRepository;
    private final FileDTOMapper fileDTOMapper;

    //TODO resolve xlsx issue
    @GetMapping(path = "/schedule/{typeSchedule}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getScheduleXlsx(@PathVariable("typeSchedule") String typeSchedule) throws IOException {
        StringBuilder path = new StringBuilder(System.getProperty("user.home") + "/Downloads/IIT_test_files/");
        StringBuilder fileName = new StringBuilder();

        switch (typeSchedule) {
            case "iit" -> {
                path.append("schedule/schedule.xlsx");
                fileName.append("schedule.xlsx");
            }
            case "feis1" -> {
                path.append("scheduleFeis/schedule1.xlsx");
                fileName.append("schedule1.xlsx");
            }
            case "feis2" -> {
                path.append("scheduleFeis/schedule2.xlsx");
                fileName.append("schedule2.xlsx");
            }
            case "feis3" -> {
                path.append("scheduleFeis/schedule3.xlsx");
                fileName.append("schedule3.xlsx");
            }
            case "feis4" -> {
                path.append("scheduleFeis/schedule4.xlsx");
                fileName.append("schedule.4xlsx");
            }
            default -> {
                return ResponseEntity.notFound().build();
            }
        }

        Path filePath = Paths.get(path.toString());
        var fileContent = Files.readAllBytes(filePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("fileName", "schedule.xlsx");

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/schedule/pdf/{typeSchedule}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getSchedulePdf(@PathVariable("typeSchedule") String typeSchedule) throws IOException {
        StringBuilder path = new StringBuilder(System.getProperty("user.home") + "/Downloads/IIT_test_files/");
        StringBuilder fileName = new StringBuilder();

        switch (typeSchedule) {
            case "iit":
                path.append("schedule/schedule.pdf");
                fileName.append("schedule.pdf");
                break;
            case "feis1":
                path.append("scheduleFeis/schedule1.pdf");
                fileName.append("schedule1.pdf");
                break;
            case "feis2":
                path.append("scheduleFeis/schedule2.pdf");
                fileName.append("schedule2.pdf");
                break;
            case "feis3":
                path.append("scheduleFeis/schedule3.pdf");
                fileName.append("schedule3.pdf");
                break;
            case "feis4":
                path.append("scheduleFeis/schedule4.pdf");
                fileName.append("schedule4.pdf");
                break;
            default:
                return ResponseEntity.notFound().build();
        }

        Path filePath = Paths.get(path.toString());
        var fileContent = Files.readAllBytes(filePath);

        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }


    @Operation(
            summary = "Установка аватара",
            description = "Устанавливает аватар для пользователя с данным username")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешная операция"),
            @ApiResponse(responseCode = "403", description = "Вы не можете изменить фото профиля другого пользователя")
    })
    @PostMapping("/users/{username}/setAvatar")
    public ResponseEntity<String> savePhoto(
            @RequestParam MultipartFile file,
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails user
    ){
        if(user.getUsername() == null){
            return new ResponseEntity<>("You cannot change another user's profile picture", HttpStatus.FORBIDDEN);
        }
        if(user.getUsername().equals(username)){
            User userDb = userService.findByUsername(username);
            var response = userService.saveUserPhoto(userDb, file);
            return ResponseEntity.created(URI.create(response)).body(response);
        }else{
            return new ResponseEntity<>("You cannot change another user's profile picture", HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping(path = "/users/{username}/getAvatar", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> getUserAvatar(
            @PathVariable String username
    ) throws IOException {
        return userService.getUserAvatar(username);
    }


    @GetMapping("/users/getAllTeachers")
    public List<UserDTO> getAllTeachers(){
        return userService.getAllTeachers();
    }

    @PreAuthorize("hasRole('TEACHER')")
    @PostMapping("/fileSystem/upload")
    public ResponseEntity<?> saveFile(
            @RequestParam MultipartFile file,
            @RequestParam FileType fileType,
            @RequestParam Discipline discipline,
            @AuthenticationPrincipal UserDetails user
    ){
        return new ResponseEntity<>(fileService.upload(file, fileType, discipline, user), HttpStatus.CREATED);
    }

    @GetMapping("/fileSystem/{id}")
    public ResponseEntity<?> getFile(@PathVariable Integer id){
        return new ResponseEntity<>(fileService.getFileById(id), HttpStatus.OK);
    }

    @GetMapping("/fileSystem/getAllMaterials")
    public ResponseEntity<?> getAllMaterials(
            @RequestParam(required = false) String teacher
    ){
        List<FileDTO> response = (teacher != null)
                ? fileService.getAllMaterialsByUser(userService.findByUsername(teacher))
                : fileService.getAllMaterials();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fileSystem/getExamQuestions")
    public ResponseEntity<?> getAllExamQuestions(
            @RequestParam(required = false) String teacher
    ){
        List<FileDTO> response = (teacher != null)
                ? fileService.getALlExamQuestionsByUser(userService.findByUsername(teacher))
                : fileService.getAllExamQuestions();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/fileSystem/getLabWorks")
    public ResponseEntity<?> getAllLabWorks(
            @RequestParam(required = false) String teacher
    ){
        List<FileDTO> response = (teacher != null)
                ? fileService.getAllLabWorksByUser(userService.findByUsername(teacher))
                : fileService.getAllLabWorks();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
