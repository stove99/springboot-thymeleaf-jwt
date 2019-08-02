package io.github.stove99.jwt_sample.login;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import io.github.stove99.jwt_sample.service.JWTService;

/**
 * 로그인 유저 정보 쉽게 가져오기 위한 Argument Resolver
 */
@Component
public class LoginUserResolver implements HandlerMethodArgumentResolver {
    @Autowired
    private JWTService jwtService;

    @Override
    public boolean supportsParameter(MethodParameter param) {
        return param.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter param, ModelAndViewContainer mvc, NativeWebRequest nreq,
            WebDataBinderFactory dbf) throws Exception {
        final Map<String, Object> resolved = new HashMap<>();

        HttpServletRequest req = (HttpServletRequest) nreq.getNativeRequest();

        // 쿠키에 토큰이 있는 경우 꺼내서 verify 후 로그인 정보 리턴
        Arrays.stream(req.getCookies()).filter(cookie -> cookie.getName().equals(LoginCheck.COOKIE_NAME))
                .map(Cookie::getValue).findFirst().ifPresent(token -> {
                    Map<String, Object> info = jwtService.verify(token);

                    // @LoginUser String id, @LoginUser String name
                    if (param.getParameterType().isAssignableFrom(String.class)) {
                        resolved.put("resolved", info.get(param.getParameterName()));
                    }
                    // @LoginUser User user
                    else if (param.getParameterType().isAssignableFrom(User.class)) {
                        User user = User.builder().id((String) info.get("id")).name((String) info.get("name")).build();

                        resolved.put("resolved", user);
                    }
                });

        return resolved.get("resolved");
    }
}