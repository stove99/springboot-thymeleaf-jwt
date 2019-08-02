package io.github.stove99.jwt_sample.login;

import java.util.Arrays;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.ModelAndViewDefiningException;

import io.github.stove99.jwt_sample.service.JWTService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;

/**
 * 로그인 여부 체크 인터셉터
 */
@Component
@Slf4j
public class LoginCheck implements HandlerInterceptor {
    public static final String COOKIE_NAME = "login_token";

    @Autowired
    private JWTService jwtService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws ModelAndViewDefiningException {

        String token = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals(LoginCheck.COOKIE_NAME)).findFirst().map(Cookie::getValue)
                .orElse("dummy");

        log.info("token : {}", token);

        try {
            Map<String, Object> info = jwtService.verify(token);

            // View 에서 session.id 처럼 로그인 정보 쉽게 가져다 쓸수 있도록 request 에 verify 한 사용자 정보 설정
            User user = User.builder().id((String) info.get("id")).name((String) info.get("name")).build();

            // view 에서 login.id 로 접근가능
            request.setAttribute("login", user);
        } catch (ExpiredJwtException ex) {
            log.error("토근이 만료됨");

            ModelAndView mav = new ModelAndView("login");
            mav.addObject("return_url", request.getRequestURI());

            throw new ModelAndViewDefiningException(mav);
        } catch (JwtException ex) {
            log.error("비정상 토큰");

            ModelAndView mav = new ModelAndView("login");

            throw new ModelAndViewDefiningException(mav);
        }

        return true;
    }
}