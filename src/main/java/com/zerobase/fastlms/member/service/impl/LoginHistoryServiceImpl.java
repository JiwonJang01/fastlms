package com.zerobase.fastlms.member.service.impl;

import com.zerobase.fastlms.member.entity.LoginHistory;
import com.zerobase.fastlms.member.repository.LoginHistoryRepository;
import com.zerobase.fastlms.member.service.LoginHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LoginHistoryServiceImpl implements LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    @Override
    public void saveLoginHistory(String userId, HttpServletRequest request) {
        try {
            String clientIp = getClientIp(request);
            String userAgent = getUserAgent(request);

            LoginHistory loginHistory = LoginHistory.builder()
                    .userId(userId)
                    .loginDt(LocalDateTime.now())
                    .clientIp(clientIp)
                    .userAgent(userAgent)
                    .build();

            loginHistoryRepository.save(loginHistory);

        } catch (Exception e) {
        }
    }

    @Override
    public List<LoginHistory> getLoginHistory(String userId) {
        return loginHistoryRepository.findByUserIdOrderByLoginDtDesc(userId);
    }

    @Override
    public LocalDateTime getLastLoginDate(String userId) {
        return loginHistoryRepository.findLastLoginDateByUserId(userId);
    }

    @Override
    public String getClientIp(HttpServletRequest request) {
        String clientIp = null;

        // X-Forwarded-For 헤더 확인 (프록시 환경)
        clientIp = request.getHeader("X-Forwarded-For");
        if (hasText(clientIp) && !"unknown".equalsIgnoreCase(clientIp)) {
            // 첫 번째 IP가 실제 클라이언트 IP
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

    @Override
    public String getUserAgent(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");

        // UserAgent가 너무 길 경우 500자로 제한
        if (hasText(userAgent) && userAgent.length() > 500) {
            userAgent = userAgent.substring(0, 500);
        }

        return userAgent;
    }

    // StringUtils.hasText 대체 메서드
    private boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}