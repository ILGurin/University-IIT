package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.ProfilePhoto;
import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.entity.UserDTO;
import com.bstu.UniversityIIT.repository.ProfilePhotoRepository;
import com.bstu.UniversityIIT.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private String photoDirectory = System.getProperty("user.home") + "/Downloads/IIT_test_files";

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

    public void setUserPhoto(User user, MultipartFile file){
        saveUserPhoto(user, file);
    }

    public void saveUserPhoto(User user, MultipartFile file){
        var directory = photoDirectory + "/" + user.getUsername() + "/" + file.getOriginalFilename();
        var photo = ProfilePhoto.builder()
                .user(user)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(directory)
                .build();
        profilePhotoRepository.save(photo);
    }

    public String uploadPhoto(MultipartFile file){
        return photoFunction.apply("profile", file);
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> photoFunction = (id, image) -> {
        String filename = id + fileExtension.apply(image.getOriginalFilename()); //id -> name
        try {
            Path fileStorageLocation = Paths.get(photoDirectory).toAbsolutePath().normalize();

            if(!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }

            Files.copy(image.getInputStream(), fileStorageLocation.resolve(filename), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/users/image/" + id + fileExtension.apply(image.getOriginalFilename())).toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to save image");
        }
    };
}
