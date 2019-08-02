package io.github.stove99.jwt_sample;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import io.github.stove99.jwt_sample.login.LoginCheck;
import io.github.stove99.jwt_sample.login.LoginUserResolver;

@Configuration
public class DemoConfig implements WebMvcConfigurer {

    @Autowired
    private LoginUserResolver loginUserResolver;

    @Autowired
    LoginCheck loginCheck;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheck).addPathPatterns("", "/**").excludePathPatterns("/login", "/resources/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginUserResolver);
    }
}