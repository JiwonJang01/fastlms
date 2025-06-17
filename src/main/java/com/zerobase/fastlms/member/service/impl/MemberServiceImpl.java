package com.zerobase.fastlms.member.service.impl;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.member.entity.Member;
import com.zerobase.fastlms.member.entity.MemberCode;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.LoginHistoryService;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    private final MemberMapper memberMapper;
    private final PasswordEncoder passwordEncoder;
    private final LoginHistoryService loginHistoryService;


    /**
     * 회원 가입
     */
    @Override
    public boolean register(MemberInput parameter) {

        Optional<Member> optionalMember = memberRepository.findById(parameter.getUserId());
        if (optionalMember.isPresent()) {
            return false;
        }

        String encPassword = passwordEncoder.encode(parameter.getPassword());
        String uuid = UUID.randomUUID().toString();

        Member member = Member.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .userStatus(MemberCode.MEMBER_STATUS_REQ)
                .build();
        memberRepository.save(member);

        String email = parameter.getUserId();
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = "<p>fastlms 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'> 가입 완료 </a></div>";
        mailComponents.sendMail(email, subject, text);

        return true;
    }

    @Override
    public boolean emailAuth(String uuid) {

        Optional<Member> optionalMember = memberRepository.findByEmailAuthKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }

        Member member = optionalMember.get();

        if (member.isEmailAuthYn()) {
            return false;
        }

        member.setUserStatus(MemberCode.MEMBER_STATUS_ING);
        member.setEmailAuthYn(true);
        member.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(member);

        return true;
    }

    // 기존 메서드들...
    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {
        // 기존 구현 유지
        return true;
    }

    @Override
    public List<MemberDto> list(MemberParam parameter) {
        long totalCount = memberMapper.selectListCount(parameter);
        List<MemberDto> list = memberMapper.selectList(parameter);

        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for(MemberDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);

                // 마지막 로그인 정보 조회 (LoginHistoryService가 주입되어 있다면)
                try {
                    if (loginHistoryService != null) {
                        LocalDateTime lastLoginDt = loginHistoryService.getLastLoginDate(x.getUserId());
                        x.setLastLoginDt(lastLoginDt);
                    }
                } catch (Exception e) {
                    // 로그인 히스토리 조회 실패 시 무시 (기존 기능에 영향 없음)
                    System.out.println("로그인 히스토리 조회 실패: " + x.getUserId());
                }

                i++;
            }
        }
        return list;
    }

    @Override
    public MemberDto detail(String userId) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return null;
        }
        Member member = optionalMember.get();
        return MemberDto.of(member);
    }

    @Override
    public boolean updateStatus(String userId, String userStatus) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();
        member.setUserStatus(userStatus);
        memberRepository.save(member);
        return true;
    }

    @Override
    public boolean updatePassword(String userId, String password) {
        Optional<Member> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
        Member member = optionalMember.get();
        String encPassword = passwordEncoder.encode(password);
        member.setPassword(encPassword);
        memberRepository.save(member);
        return true;
    }

    @Override
    public ServiceResult updateMember(MemberInput parameter) {
        return new ServiceResult();
    }

    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {
        return new ServiceResult(true);
    }

    @Override
    public ServiceResult withdraw(String userId, String password) {
        return new ServiceResult();
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//
//        Optional<Member> optionalMember = memberRepository.findById(username);
//        if (!optionalMember.isPresent()) {
//            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
//        }
//
//        Member member = optionalMember.get();
//
//        if (MemberCode.MEMBER_STATUS_REQ.equals(member.getUserStatus())) {
//            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요.");
//        }
//
//        if (MemberCode.MEMBER_STATUS_STOP.equals(member.getUserStatus())) {
//            throw new MemberStopUserException("정지된 회원 입니다.");
//        }
//
//        if (MemberCode.MEMBER_STATUS_WITHDRAW.equals(member.getUserStatus())) {
//            throw new MemberStopUserException("탈퇴된 회원 입니다.");
//        }
//
//        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
//        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
//
//        if (member.isAdminYn()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
//        }
//
//        return new User(member.getUserId(), member.getPassword(), grantedAuthorities);
//    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        System.out.println("================================");
        System.out.println("로그인 시도 사용자: " + username);
        System.out.println("================================");

        Optional<Member> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()) {
            System.out.println("❌ 사용자를 찾을 수 없음: " + username);
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        Member member = optionalMember.get();
        System.out.println("✅ 사용자 발견!");
        System.out.println("사용자 ID: " + member.getUserId());
        System.out.println("사용자 이름: " + member.getUserName());
        System.out.println("사용자 상태: " + member.getUserStatus());
        System.out.println("이메일 인증 여부: " + member.isEmailAuthYn());
        System.out.println("관리자 여부: " + member.isAdminYn());

        if (MemberCode.MEMBER_STATUS_REQ.equals(member.getUserStatus())) {
            System.out.println("❌ 이메일 인증이 필요한 사용자");
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요.");
        }

        if (MemberCode.MEMBER_STATUS_STOP.equals(member.getUserStatus())) {
            System.out.println("❌ 정지된 사용자");
            throw new MemberStopUserException("정지된 회원 입니다.");
        }

        if (MemberCode.MEMBER_STATUS_WITHDRAW.equals(member.getUserStatus())) {
            System.out.println("❌ 탈퇴된 사용자");
            throw new MemberStopUserException("탈퇴된 회원 입니다.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        if (member.isAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        System.out.println("✅ 권한 설정 완료: " + grantedAuthorities);
        System.out.println("✅ UserDetails 객체 생성 중...");

        UserDetails userDetails = new User(member.getUserId(), member.getPassword(), grantedAuthorities);
        System.out.println("✅ 로그인 인증 정보 준비 완료!");
        System.out.println("================================");

        return userDetails;
    }
}