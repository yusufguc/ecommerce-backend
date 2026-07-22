package com.ecommerce.backend.exception.handler;

import com.ecommerce.backend.exception.base.BaseException;
import com.ecommerce.backend.exception.message.MessageType;
import com.ecommerce.backend.exception.model.ApiError;
import com.ecommerce.backend.exception.model.ExceptionDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError<String>> handleBaseException(BaseException exception, WebRequest request) {
        MessageType messageType = exception.getMessageType();

        log.warn("BaseException: code={}, path={}, message={}",
                messageType.getCode(), path(request), exception.getMessage());

        ApiError<String> apiError = createApiError(exception.getMessage(), messageType, request);
        return ResponseEntity.status(messageType.getHttpStatus()).body(apiError);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError<String>> handleBadCredentials(BadCredentialsException exception, WebRequest request) {
        log.warn("BadCredentialsException: path={}", path(request));

        ApiError<String> apiError =
                createApiError(MessageType.INVALID_CREDENTIALS.getMessage(), MessageType.INVALID_CREDENTIALS, request);
        return ResponseEntity.status(MessageType.INVALID_CREDENTIALS.getHttpStatus()).body(apiError);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError<String>> handleUsernameNotFound(UsernameNotFoundException exception, WebRequest request) {
        log.warn("UsernameNotFoundException: path={}", path(request));

        ApiError<String> apiError =
                createApiError(MessageType.INVALID_CREDENTIALS.getMessage(), MessageType.INVALID_CREDENTIALS, request);
        return ResponseEntity.status(MessageType.INVALID_CREDENTIALS.getHttpStatus()).body(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError<String>> handleAccessDenied(AccessDeniedException exception, WebRequest request) {
        log.warn("AccessDeniedException: path={}", path(request));

        ApiError<String> apiError =
                createApiError(MessageType.ACCESS_DENIED.getMessage(), MessageType.ACCESS_DENIED, request);
        return ResponseEntity.status(MessageType.ACCESS_DENIED.getHttpStatus()).body(apiError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError<String>> handleGeneralException(Exception exception, WebRequest request) {
        log.error("Unhandled exception: path={}", path(request), exception);

        ApiError<String> apiError =
                createApiError(MessageType.GENERAL_EXCEPTION.getMessage(), MessageType.GENERAL_EXCEPTION, request);
        return ResponseEntity.status(MessageType.GENERAL_EXCEPTION.getHttpStatus()).body(apiError);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException exception,
                                                                    HttpHeaders headers,
                                                                    HttpStatusCode status,
                                                                    WebRequest request) {
        Map<String, List<String>> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError ->
                errors.computeIfAbsent(fieldError.getField(), key -> new ArrayList<>())
                        .add(fieldError.getDefaultMessage()));

        log.warn("ValidationException: path={}, errors={}", path(request), errors);

        ApiError<Map<String, List<String>>> apiError =
                createApiError(errors, MessageType.VALIDATION_EXCEPTION, request);
        return ResponseEntity.status(MessageType.VALIDATION_EXCEPTION.getHttpStatus()).body(apiError);
    }

    private <E> ApiError<E> createApiError(E message, MessageType messageType, WebRequest request) {
        ApiError<E> apiError = new ApiError<>();
        apiError.setStatus(messageType.getHttpStatus().value());
        apiError.setErrorCode(messageType.getCode());

        ExceptionDetail<E> detail = new ExceptionDetail<>();
        detail.setPath(path(request));
        detail.setTimestamp(Instant.now());
        detail.setMessage(message);

        apiError.setException(detail);
        return apiError;
    }

    private String path(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}
