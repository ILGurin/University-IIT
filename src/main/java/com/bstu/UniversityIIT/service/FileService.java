package com.bstu.UniversityIIT.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@RequiredArgsConstructor
public class FileService {
    private final String SCHEDULE_PATH = System.getProperty("user.home") + "/Downloads/IIT_test_files/schedule";
    private final String SCHEDULE_FEIS_PATH = System.getProperty("user.home") + "/Downloads/IIT_test_files/scheduleFeis";

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
            case "iit" -> sb.append("schedule.xlsx");
            case "feis1" -> sb.append("schedule1.xlsx");
            case "feis2" -> sb.append("schedule2.xlsx");
            case "feis3" -> sb.append("schedule3.xlsx");
            case "feis4" -> sb.append("schedule4.xlsx");
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
}
