package com.pathway.JobPathway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        String uploadAbsolutePath = uploadPath.toUri().toString();

        registry.addResourceHandler("/resumes/**")
                .addResourceLocations(uploadAbsolutePath);
        
        // Add handler for profile pictures
        Path profilePath = Paths.get(uploadDir, "profiles").toAbsolutePath().normalize();
        String profileAbsolutePath = profilePath.toUri().toString();
        
        registry.addResourceHandler("/profiles/**")
                .addResourceLocations(profileAbsolutePath);
    }
}
