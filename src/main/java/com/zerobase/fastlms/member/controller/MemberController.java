package com.zerobase.fastlms.member.controller;


import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.course.dto.TakeCourseDto;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.course.service.TakeCourseService;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {
    
    private final MemberService memberService;
    private final TakeCourseService takeCourseService;
    
    @RequestMapping("/member/login")
    public String login() {
        
        return "member/login";
    }
    
    @GetMapping("/member/find-password")
    public String findPassword() {
        
        return "member/find_password";
    }
    
    @PostMapping("/member/find-password")
    public String findPasswordSubmit(Model model, ResetPasswordInput parameter) {
        
        boolean result = memberService.sendResetPassword(parameter);
        model.addAttribute("result", result);
        
        return "member/find_password_result";
    }
    
    @GetMapping("/member/register")
    public String register() {
        
        return "member/register";
    }

    @PostMapping("/member/register")
    public String registerSubmit(Model model, HttpServletRequest request, MemberInput parameter) {

        log.info("회원가입 시도: {}", parameter.getUserId());

        try {
            boolean result = memberService.register(parameter);

            if (result) {
                log.info("회원가입 성공: {}", parameter.getUserId());
                model.addAttribute("result", true);
                model.addAttribute("message", "회원가입이 완료되었습니다. 이메일을 확인하여 가입을 완료해주세요.");
                return "member/register_complete";
            } else {
                log.warn("회원가입 실패 - 중복 아이디: {}", parameter.getUserId());
                model.addAttribute("result", false);
                model.addAttribute("message", "이미 사용 중인 아이디입니다.");
                return "member/register";
            }

        } catch (Exception e) {
            log.error("회원가입 중 오류 발생: {}", e.getMessage(), e);
            model.addAttribute("result", false);
            model.addAttribute("message", "회원가입 중 오류가 발생했습니다. 다시 시도해주세요.");
            return "member/register";
        }
    }
    
    // http://www.naver.com/news/list.do?id=123&key=124&text=쿼리
    // https://
    // 프로토콜://도메인(IP)/news/list.do?쿼리스트링(파라미터)
    // https://www.naver.com/cafe/detail.do?id=1111
    // https://www.naver.com/cafe/detail.do?id=2222
    
    
    @GetMapping("/member/email-auth")
    public String emailAuth(Model model, HttpServletRequest request) {
        
        String uuid = request.getParameter("id");
        System.out.println(uuid);
        
        boolean result = memberService.emailAuth(uuid);
        model.addAttribute("result", result);
        
        return "member/email_auth";
    }
    
    @GetMapping("/member/info")
    public String memberInfo(Model model, Principal principal) {
        
        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);
        
        model.addAttribute("detail", detail);
        
        return "member/info";
    }
    
    @PostMapping("/member/info")
    public String memberInfoSubmit(Model model
            , MemberInput parameter
            , Principal principal) {
        
        String userId = principal.getName();
        parameter.setUserId(userId);
        
        ServiceResult result = memberService.updateMember(parameter);
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }
        return "redirect:/member/info";
    }
    
    @GetMapping("/member/password")
    public String memberPassword(Model model, Principal principal) {
        
        String userId = principal.getName();
        MemberDto detail = memberService.detail(userId);
        
        model.addAttribute("detail", detail);
        
        return "member/password";
    }
    
    @PostMapping("/member/password")
    public String memberPasswordSubmit(Model model
            , MemberInput parameter
            , Principal principal) {
        
        String userId = principal.getName();
        parameter.setUserId(userId);
        
        ServiceResult result = memberService.updateMemberPassword(parameter);
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }
        
        return "redirect:/member/info";
    }
    
    
    @GetMapping("/member/takecourse")
    public String memberTakeCourse(Model model, Principal principal) {
        
        String userId = principal.getName();
        List<TakeCourseDto> list = takeCourseService.myCourse(userId);
        
        model.addAttribute("list", list);
        
        return "member/takecourse";
    }
    
    
    @GetMapping("/member/withdraw")
    public String memberWithdraw(Model model) {
        
        return "member/withdraw";
    }
    
    @PostMapping("/member/withdraw")
    public String memberWithdrawSubmit(Model model
            , MemberInput parameter
            , Principal principal) {
        
        String userId = principal.getName();
        
        ServiceResult result = memberService.withdraw(userId, parameter.getPassword());
        if (!result.isResult()) {
            model.addAttribute("message", result.getMessage());
            return "common/error";
        }
        
        return "redirect:/member/logout";
    }
}
