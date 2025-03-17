package com.devsoft.rgdi_store.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import com.devsoft.rgdi_store.config.UploadConfig;

@Controller
public class ImageUploadController {
    private final Path uploadConfig;

    public ImageUploadController(UploadConfig uploadConfig) {
        this.uploadConfig = Paths.get(uploadConfig.getUploadDir())
                .toAbsolutePath().normalize();
    }    

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
      String fileName = StringUtils.cleanPath(file.getOriginalFilename());

      try {
        Path targetLocation = uploadConfig.resolve(fileName);
        file.transferTo(targetLocation);        
        
        return ResponseEntity.ok("File uploaded successfully");
      } catch (IOException ex) {
        ex.printStackTrace();
        return ResponseEntity.badRequest().body("File upload failed.");
      }
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() throws IOException {
      List<String> fileNames = Files.list(uploadConfig)
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toList());

      return ResponseEntity.ok(fileNames);
    }
}