package com.devsoft.rgdi_store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.devsoft.rgdi_store") // Ajuste o pacote conforme necessário
public class RgdiStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(RgdiStoreApplication.class, args);
    }
}
