// Utilizado no Upload de imagens
package com.devsoft.rgdi_store.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "file") // Vinculado ao "application-producao.properties"
public class UploadConfig {
    
	// Fornece o diretório de upload
	private String uploadDir;

	  public String getUploadDir() {
	    return uploadDir;
	  }

	  public void setUploadDir(String uploadDir) {
	    this.uploadDir = uploadDir;
	  }
}