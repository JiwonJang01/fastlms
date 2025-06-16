package com.zerobase.fastlms.member.dto;

import com.zerobase.fastlms.member.entity.LoginHistory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class LoginHistoryDto {

    private Long id;
    private String userId;
    private LocalDateTime loginDt;
    private String clientIp;
    private String userAgent;

    public static LoginHistoryDto of(LoginHistory loginHistory) {
        return LoginHistoryDto.builder()
                .id(loginHistory.getId())
                .userId(loginHistory.getUserId())
                .loginDt(loginHistory.getLoginDt())
                .clientIp(loginHistory.getClientIp())
                .userAgent(loginHistory.getUserAgent())
                .build();
    }

    public String getLoginDtText() {
        if (loginDt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm");
            return loginDt.format(formatter);
        }
        return "";
    }

    // UserAgent를 짧게 표시 (브라우저 정보만)
    public String getUserAgentShort() {
        if (userAgent == null || userAgent.isEmpty()) {
            return "";
        }

        // Chrome, Firefox, Safari, Edge 등 브라우저 정보 추출
        if (userAgent.contains("Chrome")) {
            return "Chrome";
        } else if (userAgent.contains("Firefox")) {
            return "Firefox";
        } else if (userAgent.contains("Safari") && !userAgent.contains("Chrome")) {
            return "Safari";
        } else if (userAgent.contains("Edge")) {
            return "Edge";
        } else {
            return "기타";
        }
    }
}