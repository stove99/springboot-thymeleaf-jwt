package io.github.stove99.jwt_sample;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import io.github.stove99.jwt_sample.service.JWTService;
import lombok.extern.slf4j.Slf4j;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class DemoApplicationTests {
    @Autowired
    JWTService service;

    @Test
    public void contextLoads() {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "홍길동");
        body.put("email", "hong@gmail.com");

        String token = service.token(body);

        log.info("token : {}", token);

        log.info("verify : {}", service.verify(
                "eyJhbGciOiJIUzUxMiJ9.eyJuYW1lIjoi7ZmN6ri464-ZIiwiZXhwIjoxNTY0NzI1MzkzLCJlbWFpbCI6ImhvbmdAZ21haWwuY29tIn0.YtLf0ZGkvXSkqxljsd9e7NuShrVq0dKCpOFs1EWGn1HVQpg3cbDd0bZZD9b0dX3xzpLbWDO18pG8i-rpM3MyYw"));
    }

}
