package com.zerobase.fastlms.banner.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class Banner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "banner_name")
    private String bannerName;

    @Column(name = "banner_file_path")
    private String bannerFilePath;

    @Column(name = "banner_url_path")
    private String bannerUrlPath;

    @Column(name = "banner_alt_text")
    private String bannerAltText;

    @Column(name = "banner_url")
    private String bannerUrl;

    @Column(name = "banner_target")
    private String bannerTarget; // _blank, _self

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "display_yn")
    private boolean displayYn;

    @Column(name = "reg_dt")
    private LocalDateTime regDt;

    @Column(name = "udt_dt")
    private LocalDateTime udtDt;
}
