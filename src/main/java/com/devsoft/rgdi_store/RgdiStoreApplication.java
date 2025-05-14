package com.devsoft.rgdi_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import com.devsoft.rgdi_store.config.UploadConfig;

@SpringBootApplication
@EnableConfigurationProperties(UploadConfig.class)  // Habilita o mapeamento de propriedades - Usado nos testes
public class RgdiStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(RgdiStoreApplication.class, args);
    }
}
