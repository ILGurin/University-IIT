package com.bstu.UniversityIIT.controller;

import com.bstu.UniversityIIT.entity.User;
import com.bstu.UniversityIIT.entity.UserDTO;
import com.bstu.UniversityIIT.service.FileService;
import com.bstu.UniversityIIT.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final UserService userService;
    private final FileService fileService;
    private final String PHOTO_DIRECTORY = System.getProperty("user.home") + "/Downloads/IIT_test_files/";


    @GetMapping
    @PreAuthorize("hasAuthority('admin:read')")
    public List<UserDTO> getAllUsers(){
        return userService.getAllUsers();
    }

    @PutMapping("/updateUser/{id}")
    public ResponseEntity<?> updateUser(
            @PathVariable Integer id,
            @RequestBody User user
    ){

        return userService.updateUser(id, user);
    }

    @DeleteMapping("/deleteUser/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id){
        return userService.deleteById(id);
    }


    /*@PostMapping("/photo")
    @PreAuthorize("hasAuthority('admin:create')")
    public ResponseEntity<String> uploadPhoto(@RequestParam("file")MultipartFile file){
        return ResponseEntity.ok().body(userService.uploadPhoto(file));
    }*/

    /*@GetMapping(path = "/contacts/image/{filename}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public byte[] getPhoto(@PathVariable("filename") String filename) throws IOException{
        return Files.readAllBytes(Paths.get(PHOTO_DIRECTORY + filename));
    }*/


    @PostMapping("/schedule")
    public ResponseEntity<String> uploadSchedule(
            @RequestParam("file") MultipartFile file,
            @RequestParam("typeSchedule") String typeSchedule
    ){
        if(file == null){
            return ResponseEntity.badRequest().body("the \"file\" parameter is required");
        }
        //return ResponseEntity.created().body();
        return ResponseEntity.ok().body(fileService.uploadFile(file, typeSchedule));
    }
}
