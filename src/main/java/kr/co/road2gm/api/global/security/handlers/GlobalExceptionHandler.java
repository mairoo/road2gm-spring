package kr.co.road2gm.api.global.security.handlers;

import jakarta.servlet.http.HttpServletRequest;
import kr.co.road2gm.api.global.common.constants.ErrorCode;
import kr.co.road2gm.api.global.response.error.ErrorResponse;
import kr.co.road2gm.api.global.response.error.exception.ApiException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object>
    handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                 @NonNull HttpHeaders headers,
                                 @NonNull HttpStatusCode status,
                                 @NonNull WebRequest request) {
        super.handleMethodArgumentNotValid(ex, headers, status, request);

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, servletRequest.getRequestURI(),
                                       ex.getBindingResult()));
    }

    @Override
    protected ResponseEntity<Object>
    handleNoResourceFoundException(@NonNull NoResourceFoundException ex,
                                   @NonNull HttpHeaders headers,
                                   @NonNull HttpStatusCode status,
                                   @NonNull WebRequest request) {
        super.handleNoResourceFoundException(ex, headers, status, request);

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, servletRequest.getRequestURI()));
    }

    @Override
    protected ResponseEntity<Object>
    handleHttpMessageNotReadable(@NonNull HttpMessageNotReadableException ex,
                                 @NonNull HttpHeaders headers,
                                 @NonNull HttpStatusCode status,
                                 @NonNull WebRequest request) {
        super.handleHttpMessageNotReadable(ex, headers, status, request);

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(ErrorCode.MESSAGE_BODY_NOT_FOUND, servletRequest.getRequestURI()));
    }

    @Override
    protected ResponseEntity<Object>
    handleHttpMessageNotWritable(@NonNull HttpMessageNotWritableException ex,
                                 @NonNull HttpHeaders headers,
                                 @NonNull HttpStatusCode status,
                                 @NonNull WebRequest request) {
        super.handleHttpMessageNotWritable(ex, headers, status, request);

        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(ErrorCode.JSON_SERIALIZATION_FAILURE, servletRequest.getRequestURI()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?>
    handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
                                     WebRequest request) {
        HttpServletRequest servletRequest = ((ServletWebRequest) request).getRequest();

        String parameterName = ex.getName();
        String invalidValue = ex.getValue() != null ? ex.getValue().toString() : "null";
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";

        String message = String.format("파라미터 '%s'의 값 '%s'을(를) %s(으)로 변환할 수 없습니다",
                                       parameterName, invalidValue, requiredType);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(HttpStatus.BAD_REQUEST,
                                       servletRequest.getRequestURI(),
                                       message));
    }

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<?>
    handleApiException(ApiException e,
                       HttpServletRequest request) {
        return ResponseEntity
                .status(e.getErrorCode().getStatus())
                .body(ErrorResponse.of(e.getErrorCode(), request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<?>
    handleException(Exception e,
                    HttpServletRequest request) {
        log.error(e.getMessage(), e);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(ErrorCode.UNEXPECTED_ERROR, request.getRequestURI()));
    }
}
