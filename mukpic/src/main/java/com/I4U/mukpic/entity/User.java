package com.I4U.mukpic.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_key", nullable = false, unique = true)
    private int userKey; //pk 유저 식별자

    @Column(name = "user_id", nullable = false, unique = true)
    private String userId; //유저 아이디 (구글이메일 형식 가능하게 특수문자 허용 예정)

    @Email
    @Column(name = "email", nullable = false, unique = true)
    private String email; //이메일 중복x

    @Column(name = "user_name", nullable = false, unique = true)
    private String userName; //닉네임

    @Column(name = "password", nullable = false)
    private String password; //비밀번호

    @Column(name = "image")
    private String image; //유저 프로필 이미지 url

    @Column(name = "role", nullable = false)
    private String role = "USER"; //기본 값 user 관리자는 따로 admin으로 가입

    @Column(name = "login_type", nullable = false)
    private String loginType; //로그인 유형 (local, google)

    @Column(name = "agree", nullable = false)
    private Boolean agree; //약관 동의 여부

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt; //가입 시간

    @Column(name = "updated_at")
    private LocalDateTime updatedAt; //수정 시간

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
