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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Usando o uploadConfig para pegar o diretório de uploads
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadConfig.getUploadDir() + "/");
    }
}
