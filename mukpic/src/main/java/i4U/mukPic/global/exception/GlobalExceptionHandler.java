package i4U.mukPic.global.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<Map<String, Object>> createErrorResponse(HttpStatus status, String error, String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", status.value());
        response.put("error", error);
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(BusinessLogicException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessLogicException(BusinessLogicException e) {
        ExceptionCode code = e.getExceptionCode();
        return createErrorResponse(HttpStatus.valueOf(code.getStatus()), "Business Logic Error", code.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", "Validation failed: " + errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception ex) {
        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unknown error occurred");
    }

    // 유효하지 않은 토큰 예외 처리
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidTokenException(InvalidTokenException ex) {
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid Token", ExceptionCode.JWT_TOKEN_ERROR.getMessage());
    }

    // 잘못된 매개변수 타입 예외 처리
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String parameterName = ex.getName();
        String parameterType = ex.getRequiredType().getSimpleName();
        String valueType = (ex.getValue() != null) ? ex.getValue().getClass().getSimpleName() : "null";
        String message = String.format("Parameter '%s' should be of type %s, but received type %s", parameterName, parameterType, valueType);
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Parameter", message);
    }

    // JSON 파싱 오류 처리
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        String message;
        if (cause instanceof InvalidFormatException) {
            InvalidFormatException formatException = (InvalidFormatException) cause;
            String field = formatException.getPath().get(0).getFieldName();
            String targetType = formatException.getTargetType().getSimpleName();
            String valueType = (formatException.getValue() != null) ? formatException.getValue().getClass().getSimpleName() : "null";
            message = String.format("Field '%s' should be of type %s, but received type %s", field, targetType, valueType);
        } else if (cause instanceof MismatchedInputException) {
            MismatchedInputException mismatchException = (MismatchedInputException) cause;
            String field = mismatchException.getPath().isEmpty() ? "unknown" : mismatchException.getPath().get(0).getFieldName();
            String targetType = mismatchException.getTargetType().getSimpleName();
            message = String.format("Invalid value for field '%s'. Expected type: %s", field, targetType);
        } else {
            message = "Invalid JSON format or content. Please check the request payload.";
        }
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Request Body", message);
    }

    // Enum 값 오류 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleEnumException(IllegalArgumentException ex) {
        String message = ex.getMessage() != null ? ex.getMessage() : "Provided value does not match any enum constants.";
        return createErrorResponse(HttpStatus.BAD_REQUEST, "Invalid Enum Value", message);
    }

    // 존재하지 않는 엔드포인트 요청 처리
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, Object>> handleNoHandlerFoundException(NoHandlerFoundException ex) {
        String message = "Endpoint '" + ex.getRequestURL() + "' not found";
        return createErrorResponse(HttpStatus.NOT_FOUND, "Not Found", message);
    }

}
