package i4U.mukPic.global.auth.exception;

import i4U.mukPic.global.exception.CustomException;
import i4U.mukPic.global.exception.ErrorCode;

public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}