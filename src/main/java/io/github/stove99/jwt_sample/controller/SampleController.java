package io.github.stove99.jwt_sample.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.stove99.jwt_sample.login.LoginCheck;
import io.github.stove99.jwt_sample.login.LoginUser;
import io.github.stove99.jwt_sample.service.JWTService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class SampleController {
    @Autowired
    private JWTService jwtService;

    @GetMapping("/")
    public String rootPage() {
        return "redirect:/main";
    }

    @GetMapping("login")
    public void loginPage() {
    }

    @PostMapping("login")
    public String login(@RequestParam String id, @RequestParam String pwd, HttpServletResponse res) {
        // 로그인 로직

        // 로그인 성공시 쿠키에 token 저장
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "홍길동");

        // 30분후 만료되는 jwt 만들어서 쿠키에 저장
        Cookie cookie = new Cookie(LoginCheck.COOKIE_NAME,
                jwtService.token(user, Optional.of(LocalDateTime.now().plusMinutes(30))));

        cookie.setPath("/");
        cookie.setMaxAge(Integer.MAX_VALUE);

        res.addCookie(cookie);

        return "redirect:/main";
    }

    /**
     * 로그아웃 처리 : 쿠키에서 jwt 삭제
     */
    @GetMapping
    public String logout(HttpServletResponse res) {
        Cookie cookie = new Cookie(LoginCheck.COOKIE_NAME, "");
        cookie.setPath("/");
        cookie.setMaxAge(0);

        res.addCookie(cookie);

        return "redirect:/login";
    }

    @GetMapping("main")
    public void mainPage(Model model, @LoginUser String id) {
        log.info("로그인 아이디 : {}", id);
    }
}