package com.zerobase.fastlms.member.service;

import com.zerobase.fastlms.member.entity.LoginHistory;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryService {
    /**
     * 로그인 히스토리 저장
     */
    void saveLoginHistory(String userId, HttpServletRequest request);

    /**
     * 특정 사용자의 로그인 히스토리 조회
     */
    List<LoginHistory> getLoginHistory(String userId);

    /**
     * 특정 사용자의 마지막 로그인 시간 조회
     */
    LocalDateTime getLastLoginDate(String userId);

    /**
     * 클라이언트 IP 추출
     */
    String getClientIp(HttpServletRequest request);

    /**
     * UserAgent 추출
     */
    String getUserAgent(HttpServletRequest request);
}
