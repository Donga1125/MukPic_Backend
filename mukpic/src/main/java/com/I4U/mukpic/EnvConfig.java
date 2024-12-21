package com.I4U.mukpic;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

@Component
public class EnvConfig {

    private static final Dotenv dotenv = Dotenv.load(); // .env 파일 로드

    public static String get(String key) {
        return dotenv.get(key); // .env 파일의 키 값을 반환
    }
}
