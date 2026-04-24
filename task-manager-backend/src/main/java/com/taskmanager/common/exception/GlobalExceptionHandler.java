package com.taskmanager.common.exception;

import com.taskmanager.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理各类异常，返回标准格式的 Result 响应
 *
 * @author taskmanager
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 业务异常处理
     */
    @ExceptionHandler(ServiceException.class)
    public Result<?> handleServiceException(ServiceException e, HttpServletRequest request) {
        log.error("[业务异常] 请求地址: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        return Result.error(e.getCode(), e.getMessage());
    }

    /**
     * 验证码异常处理
     */
    @ExceptionHandler(CaptchaException.class)
    public Result<?> handleCaptchaException(CaptchaException e) {
        log.warn("[验证码异常] {}", e.getMessage());
        return Result.error(500, e.getMessage());
    }

    /**
     * 权限拒绝异常处理（@PreAuthorize 返回 false 时抛出）
     * 返回 HTTP 403 状态码
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<?>> handleAccessDeniedException(AccessDeniedException e, HttpServletRequest request) {
        log.warn("[权限拒绝] 请求地址: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Result.error(HttpStatus.FORBIDDEN.value(), "没有权限，请联系管理员授权"));
    }

    /**
     * 认证异常处理（未登录或 Token 无效时抛出）
     * 返回 HTTP 401 状态码
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result<?>> handleAuthenticationException(AuthenticationException e, HttpServletRequest request) {
        log.warn("[认证失败] 请求地址: {}, 异常信息: {}", request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(HttpStatus.UNAUTHORIZED.value(), "认证失败，请重新登录"));
    }

    /**
     * 请求方式不支持
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Result<?> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        log.error("[请求方式不支持] {}", e.getMessage());
        return Result.error(405, "请求方式 '" + e.getMethod() + "' 不支持");
    }

    /**
     * 参数校验异常（@RequestBody + @Valid）
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldError() != null
                ? e.getBindingResult().getFieldError().getDefaultMessage()
                : "参数校验失败";
        log.error("[参数校验失败] {}", message);
        return Result.error(400, message);
    }

    /**
     * 参数绑定异常（@RequestParam 等）
     */
    @ExceptionHandler(BindException.class)
    public Result<?> handleBindException(BindException e) {
        String message = e.getFieldError() != null
                ? e.getFieldError().getDefaultMessage()
                : "参数绑定失败";
        log.error("[参数绑定失败] {}", message);
        return Result.error(400, message);
    }

    /**
     * 兜底：处理所有未捕获的异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e, HttpServletRequest request) {
        log.error("[系统异常] 请求地址: {}", request.getRequestURI(), e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
