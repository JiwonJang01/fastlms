package com.zerobase.fastlms.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일들을 웹에서 접근할 수 있도록 설정
        String uploadPath = System.getProperty("user.home") + "/fastlms/files/";

        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + uploadPath)
                .setCachePeriod(3600); // 1시간 캐시
    }
}