package com.example.registration.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileStorageConfig {

    @Value("${file.storage.location}")
    private String fileStorageLocation;

    @Bean
    public Path fileStorageLocation() {
        return Paths.get(fileStorageLocation).toAbsolutePath().normalize();
    }
}
