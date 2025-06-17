package com.zerobase.fastlms.main.controller;


import com.zerobase.fastlms.components.MailComponents;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.*;
import java.io.IOException;
import java.io.PrintWriter;

@RequiredArgsConstructor
@Controller
public class MainController {

    private final MailComponents mailComponents;
    
    @RequestMapping("/")
    public String index() {
        
        /*
        String email = "satcop@naver.com";
        String subject = " 안녕하세요. 제로베이스 입니다. ";
        String text = "<p>안녕하세요.</p><p>반갑습니다.</p>";
        
        mailComponents.sendMail(email, subject, text);
        */
        
        return "index";
    }
    
    
    
    @RequestMapping("/error/denied")
    public String errorDenied() {
        
        return "error/denied";
    }

    /**
     * 클라이언트 IP 추출 (과제 요구사항에 따른 RequestUtils 로직)
     */
    private String getClientIp(HttpServletRequest request) {
        String clientIp = null;

        // X-Forwarded-For 헤더 확인 (프록시 환경)
        clientIp = request.getHeader("X-Forwarded-For");
        if (hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
            clientIp = clientIp.split(",")[0].trim();
        }

        // X-Real-IP 헤더 확인
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("X-Real-IP");
        }

        // Proxy-Client-IP 헤더 확인
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }

        // WL-Proxy-Client-IP 헤더 확인
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }

        // HTTP_CLIENT_IP 헤더 확인
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }

        // HTTP_X_FORWARDED_FOR 헤더 확인
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }

        // 기본 RemoteAddr 사용
        if (!hasText(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }

    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
