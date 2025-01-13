package i4U.mukPic.global.exception;

import lombok.Getter;

@Getter
public enum ExceptionCode {

    JWT_TOKEN_ERROR (401, "토큰 오류로 UserId 추출 불가"),
    BOARD_NOT_FOUND(404, "게시글 정보를 찾을 수 없음"),
    REPLY_NOT_FOUND (404, "댓글 정보를 찾을 수 없음"),
    NOT_FOUND(404, "정보를 찾을 수 없음"),
    CATEGORY_NOT_FOUND (404, "게시글 카테고리 정보를 찾을 수 없음"),
    USER_NOT_FOUND(404, "유저 정보를 찾을 수 없음"),
    INVALID_EMAIL_ERROR(404, "이메일을 찾을 수 없음"),
    USER_NOT_ACTIVE(403, "탈퇴한 회원입니다."),
    DUPLICATE_EMAIL_ERROR (409, "이메일 중복"),
    DUPLICATE_USERNAME_ERROR (409, "닉네임 중복"),
    DUPLICATE_ERROR (409, "중복된 요청"),

    FILE_UPLOAD_ERROR(413, "파일 업로드 실패"),
    FILE_DELETE_ERROR(500, "파일 삭제 실패"),
    FILE_FORMAT_ERROR(415, "파일 확장자 오류"),
    FILE_SIZE_ERROR (413, "최대 업로드 개수 초과"),
    FILE_NOT_FOUND(404, "파일을 찾을 수 없음"),

    UNKNOWN_ERROR(1001, "토큰이 존재하지 않습니다."),
    WRONG_TYPE_TOKEN(1002, "잘못된 형식의 토큰입니다."),
    EXPIRED_TOKEN(1003, "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(1004, "변조된 토큰입니다."),
    ACCESS_DENIED(1005, "권한이 없습니다."),

    INVALID_ALLERGY_TYPE(400, "잘못된 알러지 타입입니다."),
    INVALID_CHRONIC_DISEASE_TYPE(400, "잘못된 만성 질환 타입입니다."),
    INVALID_DIETARY_PREFERENCE_TYPE(400, "잘못된 선호식단 타입입니다."),
    INVALID_CREDENTIALS(401, "아이디 또는 비밀번호가 올바르지 않습니다."),
    ALREADY_DEACTIVATED_USER(400, "이미 비활성화된 회원입니다."),
    INVALID_TOKEN_ERROR(401, "유효하지 않은 토큰입니다."),

    INVALID_REQUEST_BODY(400, "요청 본문이 잘못되었습니다."),
    AI_SERVER_ERROR(500, "AI 서버 호출 중 오류가 발생했습니다."),
    OPENAI_API_ERROR(500, "OpenAI API 호출 중 오류가 발생했습니다."),
    INVALID_PASSWORD_ERROR(400, "비밀번호가 유효하지 않습니다."),
    UNKNOWN_USER_STATUS_ERROR(400, "알 수 없는 유저 상태입니다.");

    private final int status;

    private final String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

}
