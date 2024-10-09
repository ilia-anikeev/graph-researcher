//package com.graphResearcher.controller;
//
//import com.graphResearcher.service.FileProcessingService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@CrossOrigin
//@RestController
//public class FileUploadController {
//
//    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
//    private final FileProcessingService fileProcessingService;
//
//    public FileUploadController(FileProcessingService fileProcessingService) {
//        this.fileProcessingService = fileProcessingService;
//    }
//
//    @PostMapping("/upload")
//    public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file,@RequestParam int userID) {
//        try {
//            if (file.isEmpty()) {
//                return ResponseEntity.badRequest().body("File is empty");
//            }
//
//            String responseMessage = "hi";/*fileProcessingService.processFile(file,userID);*/
//            log.info("File uploaded and processed successfully");
//            return ResponseEntity.ok(responseMessage);
//        } catch (Exception e) {
//            log.error("Error processing file", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File processing error");
//        }
//    }
//}