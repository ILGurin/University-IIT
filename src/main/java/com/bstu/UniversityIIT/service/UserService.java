package com.bstu.UniversityIIT.service;

import com.bstu.UniversityIIT.entity.ProfilePhoto;
import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.entity.DTO.UserDTO;
import com.bstu.UniversityIIT.repository.ProfilePhotoRepository;
import com.bstu.UniversityIIT.repository.TokenRepository;
import com.bstu.UniversityIIT.repository.UserRepository;
import com.bstu.UniversityIIT.service.mapper.UserDTOMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
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
    private final TokenRepository tokenRepository;
    private final ProfilePhotoRepository profilePhotoRepository;
    private final UserDTOMapper userDTOMapper;
    private String photoDirectory = System.getProperty("user.home") + "/Downloads/IIT_test_files/users";

    public List<UserDTO> getAllUsers(){
        return userRepository.findAll()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    public UserDTO getUser(Integer id){
        return userRepository.findById(id)
                .map(userDTOMapper)
                .orElseThrow();
    }

    public List<UserDTO> getAllTeachers(){
        return userRepository.findAllTeachers()
                .stream()
                .map(userDTOMapper)
                .collect(Collectors.toList());
    }

    public User findByUsername(String username){
        return this.userRepository.findByUsername(username).orElseThrow();
    }

    public void setUserPhoto(User user, MultipartFile file){
        saveUserPhoto(user, file);
    }

    @Transactional
    public String saveUserPhoto(User user, MultipartFile file){
        var directory = photoDirectory + "/" + user.getUsername();
        System.out.println(directory);
        var photo = ProfilePhoto.builder()
                .user(user)
                .name(file.getOriginalFilename())
                .type(file.getContentType())
                .filePath(directory + "/" + file.getOriginalFilename())
                .build();

        Optional<ProfilePhoto> existingPhoto = profilePhotoRepository.findByUserId(user.getId());
        ProfilePhoto profilePhoto;
        if(existingPhoto.isPresent()){
            profilePhoto = existingPhoto.orElseThrow();
            profilePhoto.setName(photo.getName());
            profilePhoto.setType(photo.getType());
            profilePhoto.setFilePath(photo.getFilePath());
            profilePhotoRepository.save(profilePhoto);
        } else {
            profilePhotoRepository.save(photo);
        }
        return uploadPhotoFunction.apply(directory, file);
    }

    public ResponseEntity<byte[]> getUserAvatar(String username) throws IOException {
        StringBuilder sb = new StringBuilder(photoDirectory); //??? String ???
        sb.append("/");
        sb.append(username);
        sb.append("/userAvatar.png");

        Path userAvatarPath = Paths.get(sb.toString());
        byte[] fileContent;
        try{
            fileContent = Files.readAllBytes(userAvatarPath);
        }catch (NoSuchFileException e){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        return new ResponseEntity<>(fileContent, HttpStatus.OK);
    }

    private final Function<String, String> fileExtension = filename -> Optional.of(filename).filter(name -> name.contains("."))
            .map(name -> "." + name.substring(filename.lastIndexOf(".") + 1)).orElse(".png");

    private final BiFunction<String, MultipartFile, String> uploadPhotoFunction = (path, image) -> {
        //String filename = id + fileExtension.apply(image.getOriginalFilename()); //id -> name
        try{
            Path fileStorageLocation = Paths.get(path).toAbsolutePath().normalize();

            if(!Files.exists(fileStorageLocation)){
                Files.createDirectories(fileStorageLocation);
            }

            Files.copy(image.getInputStream(), fileStorageLocation.resolve("userAvatar.png"), REPLACE_EXISTING);

            return ServletUriComponentsBuilder
                    .fromCurrentContextPath()
                    .path("/api/v1/users/"
                            + path.substring(path.lastIndexOf("/") + 1)
                            + "/userAvatar.png")
                    .toUriString();
        } catch (IOException e) {
            throw new RuntimeException("Unable to save image");
        }
    };

    public ResponseEntity<?> updateUser(Integer id, User user){
        var userFromDbOptional = userRepository.findById(id);
        if(userFromDbOptional.isEmpty()){
            return new ResponseEntity<>("User not found", HttpStatus.NO_CONTENT);
        }

        User userFromDb = userFromDbOptional.orElseThrow();

        if (user.getUsername() != null) {
            userFromDb.setUsername(user.getUsername());
        }
        if(user.getRole() != null){
            userFromDb.setRole(user.getRole());
        }
        userRepository.save(userFromDb);

        return new ResponseEntity<User>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<?> deleteById(Integer id){
        if (userRepository.existsById(id)) {
            tokenRepository.deleteAllByUserId(id); //Transaction
            userRepository.deleteById(id);
            return new ResponseEntity<>("User successfully deleted" , HttpStatus.OK);
        }
        return new ResponseEntity<>("This user does not exist", HttpStatus.NO_CONTENT);
    }
}
