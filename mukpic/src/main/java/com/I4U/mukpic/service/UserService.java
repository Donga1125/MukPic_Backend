package com.I4U.mukpic.service;

import com.I4U.mukpic.entity.User;
import com.I4U.mukpic.repository.UserRepository;
import com.I4U.mukpic.utils.AuthCodeGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String registerUser(User user) {
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디 입니다."); //아이디 중복체크
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일 입니다."); //이메일 중복체크
        }

        String authCode  = AuthCodeGenerator.generateCode(); //인증코드 생성

        emailService.sendEmail(
                user.getEmail(),
                "Mukpic 인증 코드",
                "당신의 인증 코드는: " + authCode
        ); //이메일 전송

        user.setPassword(passwordEncoder.encode(user.getPassword())); //비밀번호 암호화

        userRepository.save(user); //데이터베이스 저장
        return "성공적으로 회원가입이 완료 되었습니다."; //생각해보니 외국인 대상이면 안내메시지를 영어로 바꿔야 할듯
    }

}
