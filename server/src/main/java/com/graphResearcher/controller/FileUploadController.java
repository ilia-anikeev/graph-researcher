package com.graphResearcher.controller;

import com.graphResearcher.service.FileProcessingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;
@CrossOrigin
@RestController
public class FileUploadController {

    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private final FileProcessingService fileProcessingService;

    public FileUploadController(FileProcessingService fileProcessingService) {
        this.fileProcessingService = fileProcessingService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file,@RequestParam int userID) {
        try {
            if (file.isEmpty()) {
                Map<String, Object> errorResponse = Map.of("error", "File is empty");

                return ResponseEntity.badRequest().body(errorResponse);
            }

            Integer graphId = fileProcessingService.processFile(file,userID);
            Map<String, Object> response = Map.of(
                    "graphID", graphId
            );

            log.info("File uploaded and processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error processing file", e);
            Map<String, Object> errorResponse = Map.of("error", "File is empty");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}