package com.zerobase.fastlms.banner.repository;

import com.zerobase.fastlms.banner.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BannerRepository extends JpaRepository<Banner, Long> {

    /**
     * 표시 가능한 배너 목록 조회
     */
    List<Banner> findByDisplayYnTrueOrderBySortOrderAsc();

    /**
     * 관리자용 전체 배너 목록 조회
     */
    List<Banner> findAllByOrderByRegDtDesc();

    /**
     * 정렬 순서 중복 확인
     */
    @Query("SELECT COUNT(b) FROM Banner b WHERE b.sortOrder = :sortOrder AND b.id != :excludeId")
    long countBySortOrderAndIdNot(Integer sortOrder, Long excludeId);

    /**
     * 정렬 순서 중복 확인
     */
    long countBySortOrder(Integer sortOrder);
}