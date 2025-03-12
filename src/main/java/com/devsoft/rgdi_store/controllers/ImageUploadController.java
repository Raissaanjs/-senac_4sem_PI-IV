package com.devsoft.rgdi_store.controllers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.devsoft.rgdi_store.config.UploadConfig;

import jakarta.annotation.PostConstruct;

@Controller
public class ImageUploadController {

    private final UploadConfig uploadConfig;

    public ImageUploadController(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    @PostConstruct
    public void init() {
        File uploadDirFile = new File(uploadConfig.getUploadDir());
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
    }

    @PostMapping("/uploadImages")
    @ResponseBody
    public List<String> uploadImages(@RequestParam("imageFiles") MultipartFile[] files) {
        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue; // Pula arquivos vazios
            }

            try {
                // Salvar o arquivo na pasta de uploads
                Path path = Paths.get(uploadConfig.getUploadDir(), file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                imageUrls.add("/uploads/" + file.getOriginalFilename());

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return imageUrls;
    }
}
