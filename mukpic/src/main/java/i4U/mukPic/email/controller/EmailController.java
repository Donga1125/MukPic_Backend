package i4U.mukPic.email.controller;

import i4U.mukPic.email.dto.EmailCheckDTO;
import i4U.mukPic.email.dto.EmailRequestDTO;
import i4U.mukPic.email.service.EmailSendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailSendService emailSendService;

    @PostMapping("/register/email")
    public Map<String, String> mailSend(@RequestBody EmailRequestDTO emailRequestDto) {
        String code = emailSendService.joinEmail(emailRequestDto.getEmail());
        Map<String, String> response = new HashMap<>();
        response.put("code", code);
        return response;
    }

    @PostMapping("/register/emailAuth")
    public String authCheck(@RequestBody EmailCheckDTO emailCheckDto) {
        boolean isVerified = emailSendService.checkAuthNum(emailCheckDto.getEmail(), emailCheckDto.getAuthNum());
        if (isVerified) {
            return "이메일 인증 성공!";
        } else {
            throw new IllegalArgumentException("인증 실패!");
        }
    }
}
