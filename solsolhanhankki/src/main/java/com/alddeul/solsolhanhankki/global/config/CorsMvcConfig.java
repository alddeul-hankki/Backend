package com.alddeul.solsolhanhankki.global.config;


import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsMvcConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://4f62e65a137a.ngrok-free.app",
                        "https://cfd8d161bbf2.ngrok-free.app",
                        "http://localhost:4173",
                        "https://frontend-bsrb621l1-ar61208-1803s-projects.vercel.app")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                .allowedHeaders("*")
                .exposedHeaders("*")
				.allowCredentials(true)
                .maxAge(3600);
    }
}