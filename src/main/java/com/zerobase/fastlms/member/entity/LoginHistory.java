package com.zerobase.fastlms.member.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private String userId;

    @Column(name = "login_dt")
    private LocalDateTime loginDt;

    @Column(name = "client_ip")
    private String clientIp;

    @Column(name = "user_agent", length = 500)
    private String userAgent;
}
