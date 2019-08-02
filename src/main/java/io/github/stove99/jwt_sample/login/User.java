package io.github.stove99.jwt_sample.login;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class User {

    private String id;
    private String name;
}