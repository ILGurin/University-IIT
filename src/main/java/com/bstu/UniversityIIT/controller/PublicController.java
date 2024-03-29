package com.bstu.UniversityIIT.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class PublicController {

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
}
