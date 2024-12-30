package i4U.mukPic.global.auth.exception;

import i4U.mukPic.global.exception.CustomException;
import i4U.mukPic.global.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}