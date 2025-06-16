package com.zerobase.fastlms.configuration;

import com.zerobase.fastlms.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class SecurityConfiguration {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(memberService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화
                .csrf(AbstractHttpConfigurer::disable)

                // 프레임 옵션 설정
                .headers(headers -> headers
                        .frameOptions(frameOptions -> frameOptions.sameOrigin())
                )

                // 인증 설정
                .authorizeHttpRequests(authz -> authz
                        // 공개 경로
                        .requestMatchers(
                                "/",
                                "/member/register",
                                "/member/email-auth",
                                "/member/find-password",
                                "/member/reset/password",
                                "/favicon.ico",
                                "/files/**",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/debug/**"  // 디버깅용 (나중에 제거)
                        ).permitAll()

                        // 관리자 경로
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")

                        // 나머지는 인증 필요
                        .anyRequest().authenticated()
                )

                // 로그인 설정
                .formLogin(form -> form
                        .loginPage("/member/login")
                        .loginProcessingUrl("/member/login")
                        .usernameParameter("username")
                        .passwordParameter("password")
                        .successHandler(customLoginSuccessHandler)  // 커스텀 성공 핸들러 추가
                        .failureUrl("/member/login?error=true")
                        .permitAll()
                )

                // 로그아웃 설정
                .logout(logout -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/member/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                )

                // 예외 처리
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/error/denied")
                );

        return http.build();
    }
}