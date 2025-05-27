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
     // limpa os caracteres suspeitos (.., //)
      String fileName = StringUtils.cleanPath(file.getOriginalFilename());

      try {
        Path targetLocation = uploadConfig.resolve(fileName); //Define o caminho final onde o arquivo será salvo
        file.transferTo(targetLocation); // Salva o arquivo fisicamente no disco com "transferTo" 
        
        return ResponseEntity.ok("File uploaded successfully"); //Retorna resposta 200 OK, se sucesso
      } catch (IOException ex) {
        ex.printStackTrace();
        // Em caso de falha no upload, responde com erro 400
        return ResponseEntity.badRequest().body("File upload failed."); //
      }
    }

    // Lista todos as imagens presentes no diretório de upload
    @GetMapping("/list")
    public ResponseEntity<List<String>> listFiles() throws IOException {
      // Usa Files.list() para obter os arquivos do diretório	
      List<String> fileNames = Files.list(uploadConfig)
    	  //Extrai apenas os nomes (sem o caminho completo)
          .map(Path::getFileName)
          .map(Path::toString)
          .collect(Collectors.toList());

      //Retorna os nomes em uma lista com status 200 OK
      return ResponseEntity.ok(fileNames);
    }
}
