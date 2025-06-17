package com.zerobase.fastlms.banner.service;

import com.zerobase.fastlms.banner.dto.BannerDto;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.course.model.ServiceResult;

import java.util.List;

public interface BannerService {

    /**
     * 관리자용 배너 목록 조회
     */
    List<BannerDto> list();

    /**
     * 배너 상세 조회
     */
    BannerDto getById(long id);

    /**
     * 배너 등록
     */
    ServiceResult add(BannerInput parameter);

    /**
     * 배너 수정
     */
    ServiceResult set(BannerInput parameter);

    /**
     * 배너 삭제
     */
    ServiceResult del(String idList);

    /**
     * 프론트용 공개 배너 목록 조회 (정렬순서대로)
     */
    List<BannerDto> frontList();

    /**
     * 정렬 순서 중복 확인
     */
    boolean isDuplicateSortOrder(Integer sortOrder, Long excludeId);
}