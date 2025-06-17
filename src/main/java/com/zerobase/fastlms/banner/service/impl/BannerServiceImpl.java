package com.zerobase.fastlms.banner.service.impl;

import com.zerobase.fastlms.banner.dto.BannerDto;
import com.zerobase.fastlms.banner.entity.Banner;
import com.zerobase.fastlms.banner.model.BannerInput;
import com.zerobase.fastlms.banner.repository.BannerRepository;
import com.zerobase.fastlms.banner.service.BannerService;
import com.zerobase.fastlms.course.model.ServiceResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Override
    public List<BannerDto> list() {
        List<Banner> banners = bannerRepository.findAllByOrderByRegDtDesc();

        return banners.stream()
                .map(BannerDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public BannerDto getById(long id) {
        Optional<Banner> optionalBanner = bannerRepository.findById(id);
        if (optionalBanner.isPresent()) {
            return BannerDto.of(optionalBanner.get());
        }
        return null;
    }

    @Override
    public ServiceResult add(BannerInput parameter) {
        ServiceResult result = new ServiceResult();

        try {
            // 정렬 순서 중복 확인
            if (parameter.getSortOrder() != null) {
                long count = bannerRepository.countBySortOrder(parameter.getSortOrder());
                if (count > 0) {
                    result.setResult(false);
                    result.setMessage("동일한 정렬 순서가 이미 존재합니다.");
                    return result;
                }
            }

            Banner banner = Banner.builder()
                    .bannerName(parameter.getBannerName())
                    .bannerFilePath(parameter.getBannerFilePath())
                    .bannerUrlPath(parameter.getBannerUrlPath())
                    .bannerAltText(parameter.getBannerAltText())
                    .bannerUrl(parameter.getBannerUrl())
                    .bannerTarget(parameter.getBannerTarget())
                    .sortOrder(parameter.getSortOrder())
                    .displayYn(parameter.isDisplayYn())
                    .regDt(LocalDateTime.now())
                    .build();

            bannerRepository.save(banner);
            result.setResult(true);

        } catch (Exception e) {
            result.setResult(false);
            result.setMessage("배너 등록 중 오류가 발생했습니다.");
        }

        return result;
    }

    @Override
    public ServiceResult set(BannerInput parameter) {
        ServiceResult result = new ServiceResult();

        try {
            Optional<Banner> optionalBanner = bannerRepository.findById(parameter.getId());
            if (!optionalBanner.isPresent()) {
                result.setResult(false);
                result.setMessage("배너 정보가 존재하지 않습니다.");
                return result;
            }

            // 정렬 순서 중복 확인 (자기 자신 제외)
            if (parameter.getSortOrder() != null) {
                long count = bannerRepository.countBySortOrderAndIdNot(
                        parameter.getSortOrder(), parameter.getId());
                if (count > 0) {
                    result.setResult(false);
                    result.setMessage("동일한 정렬 순서가 이미 존재합니다.");
                    return result;
                }
            }

            Banner banner = optionalBanner.get();
            banner.setBannerName(parameter.getBannerName());
            banner.setBannerAltText(parameter.getBannerAltText());
            banner.setBannerUrl(parameter.getBannerUrl());
            banner.setBannerTarget(parameter.getBannerTarget());
            banner.setSortOrder(parameter.getSortOrder());
            banner.setDisplayYn(parameter.isDisplayYn());
            banner.setUdtDt(LocalDateTime.now());

            // 새 파일이 업로드된 경우에만 파일 경로 업데이트
            if (parameter.getBannerFilePath() != null && !parameter.getBannerFilePath().isEmpty()) {
                banner.setBannerFilePath(parameter.getBannerFilePath());
                banner.setBannerUrlPath(parameter.getBannerUrlPath());
            }

            bannerRepository.save(banner);
            result.setResult(true);

        } catch (Exception e) {
            result.setResult(false);
            result.setMessage("배너 수정 중 오류가 발생했습니다.");
        }

        return result;
    }

    @Override
    public ServiceResult del(String idList) {
        ServiceResult result = new ServiceResult();

        try {
            if (idList != null && !idList.isEmpty()) {
                String[] ids = idList.split(",");
                for (String strId : ids) {
                    long id = Long.parseLong(strId.trim());
                    bannerRepository.deleteById(id);
                }
            }
            result.setResult(true);

        } catch (Exception e) {
            result.setResult(false);
            result.setMessage("배너 삭제 중 오류가 발생했습니다.");
        }

        return result;
    }

    @Override
    public List<BannerDto> frontList() {
        List<Banner> banners = bannerRepository.findByDisplayYnTrueOrderBySortOrderAsc();

        return banners.stream()
                .map(BannerDto::of)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isDuplicateSortOrder(Integer sortOrder, Long excludeId) {
        if (sortOrder == null) {
            return false;
        }

        if (excludeId != null) {
            return bannerRepository.countBySortOrderAndIdNot(sortOrder, excludeId) > 0;
        } else {
            return bannerRepository.countBySortOrder(sortOrder) > 0;
        }
    }
}