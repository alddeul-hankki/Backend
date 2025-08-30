package com.alddle.ddangyo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://cfd8d161bbf2.ngrok-free.app", "http://localhost:4173", "https://frontend-bsrb621l1-ar61208-1803s-projects.vercel.app")
                .allowedMethods("*")
                .exposedHeaders("*")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}