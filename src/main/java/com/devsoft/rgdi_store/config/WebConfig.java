// Utilizado no Upload de imagens
package com.devsoft.rgdi_store.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UploadConfig uploadConfig; // A classe UploadConfig fornece o diretório de upload

    public WebConfig(UploadConfig uploadConfig) {
        this.uploadConfig = uploadConfig;
    }

    // Usa o uploadConfig para pegar o diretório de uploads
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {        
        registry.addResourceHandler("/uploads/**") // Define a URL pública que será usada para acessar os arquivos
                // Define o caminho real no disco onde os arquivos estão armazenados
        		// O prefixo file: indica que o Spring deve tratar o caminho como um recurso de sistema de arquivos
        		.addResourceLocations("file:" + uploadConfig.getUploadDir() + "/");
    }
}
