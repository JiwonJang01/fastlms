package com.zerobase.fastlms.member.repository;

import com.zerobase.fastlms.member.entity.LoginHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

    /**
     * 특정 사용자의 로그인 히스토리 조회 (최신순)
     */
    List<LoginHistory> findByUserIdOrderByLoginDtDesc(String userId);

    /**
     * 특정 사용자의 최근 N개 로그인 히스토리 조회
     */
    @Query("SELECT lh FROM LoginHistory lh WHERE lh.userId = :userId ORDER BY lh.loginDt DESC")
    List<LoginHistory> findTopNByUserId(@Param("userId") String userId, Pageable pageable);

    /**
     * 특정 사용자의 마지막 로그인 시간 조회
     */
    @Query("SELECT MAX(lh.loginDt) FROM LoginHistory lh WHERE lh.userId = :userId")
    LocalDateTime findLastLoginDateByUserId(@Param("userId") String userId);
}