package i4U.mukPic.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;

    public ErrorResponse(ExceptionCode code){
        this.status = code.getStatus();
        this.message = code.getMessage();
    }
}
