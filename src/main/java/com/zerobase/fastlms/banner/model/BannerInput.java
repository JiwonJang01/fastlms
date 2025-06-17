package com.zerobase.fastlms.banner.model;

import lombok.Data;

@Data
public class BannerInput {

    private Long id;
    private String bannerName;
    private String bannerAltText;
    private String bannerUrl;
    private String bannerTarget;
    private Integer sortOrder;
    private boolean displayYn;

    // 파일 업로드 관련
    private String bannerFilePath;
    private String bannerUrlPath;

    // 삭제용
    private String idList;
}