package com.example.wakeup_back.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /voices/** 요청을 uploads/voices/ 폴더로 매핑
        registry.addResourceHandler("/voices/**")
                .addResourceLocations("file:uploads/voices/");
    }
}