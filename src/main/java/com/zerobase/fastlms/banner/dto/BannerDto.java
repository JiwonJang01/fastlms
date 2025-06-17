package com.zerobase.fastlms.banner.dto;

import com.zerobase.fastlms.banner.entity.Banner;
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
public class BannerDto {

    private Long id;
    private String bannerName;
    private String bannerFilePath;
    private String bannerUrlPath;
    private String bannerAltText;
    private String bannerUrl;
    private String bannerTarget;
    private Integer sortOrder;
    private boolean displayYn;
    private LocalDateTime regDt;
    private LocalDateTime udtDt;

    // 추가 컬럼 (페이징용)
    private long totalCount;
    private long seq;

    public static BannerDto of(Banner banner) {
        return BannerDto.builder()
                .id(banner.getId())
                .bannerName(banner.getBannerName())
                .bannerFilePath(banner.getBannerFilePath())
                .bannerUrlPath(banner.getBannerUrlPath())
                .bannerAltText(banner.getBannerAltText())
                .bannerUrl(banner.getBannerUrl())
                .bannerTarget(banner.getBannerTarget())
                .sortOrder(banner.getSortOrder())
                .displayYn(banner.isDisplayYn())
                .regDt(banner.getRegDt())
                .udtDt(banner.getUdtDt())
                .build();
    }

    public String getRegDtText() {
        if (regDt != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
            return regDt.format(formatter);
        }
        return "";
    }

    public String getDisplayYnText() {
        return displayYn ? "공개" : "비공개";
    }

    public String getBannerTargetText() {
        if ("_blank".equals(bannerTarget)) {
            return "새창";
        } else if ("_self".equals(bannerTarget)) {
            return "현재창";
        }
        return "현재창";
    }
}