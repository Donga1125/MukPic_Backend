package i4U.mukPic.notice.controller;
import i4U.mukPic.notice.dto.NoticeRequestDto;
import i4U.mukPic.notice.dto.NoticeResponseDto;
import i4U.mukPic.notice.service.NoticeService;
import i4U.mukPic.user.entity.User;
import i4U.mukPic.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
@Validated
public class NoticeController {
    private final UserService userService;
    private final NoticeService noticeService;

    //공지사항 생성
    @PostMapping
    public ResponseEntity postNotice(@Valid @RequestBody NoticeRequestDto.Post postDto,
                                     HttpServletRequest request) {
        User user = userService.getUserFromRequest(request);
        NoticeResponseDto.DetailNotice noticeResponseDto = noticeService.createNotice(user.getUserKey(), postDto);

        return new ResponseEntity<>(noticeResponseDto, HttpStatus.OK);
    }

    //공지사항 전체 조회
    @GetMapping
    public ResponseEntity getAllNotice (){
        List<NoticeResponseDto.BriefNotice> allNoticeList = noticeService.findAll();
        return new ResponseEntity<>(allNoticeList, HttpStatus.OK);
    }

    //공지사항 상세 조회
    @GetMapping("/{noticeId}")
    public ResponseEntity getNoticeById (@PathVariable("noticeId") Long noticeId){
        NoticeResponseDto.DetailNotice noticeResponseDto = noticeService.findById(noticeId);

        return new ResponseEntity(noticeResponseDto, HttpStatus.OK);
    }

    //공지사항 수정
    @PatchMapping("/{noticeId}")
    public ResponseEntity updateNotice (@PathVariable("noticeId") Long noticeId,
                                        @Valid @RequestBody NoticeRequestDto.Patch patchDto,
                                        HttpServletRequest request){
        User user = userService.getUserFromRequest(request);
        NoticeResponseDto.DetailNotice noticeResponseDto = noticeService.updateNotice(user.getUserKey(),noticeId, patchDto);

        return new ResponseEntity<>(noticeResponseDto, HttpStatus.OK);
    }

    //공지사항 삭제
    @DeleteMapping("/{noticeId}")
    public ResponseEntity deleteNotice (@PathVariable("noticeId") Long noticeId,
                                        HttpServletRequest request){
        User user = userService.getUserFromRequest(request);
        noticeService.deleteNotice(user.getUserKey(),noticeId);
        return ResponseEntity.ok().build();
    }

}
