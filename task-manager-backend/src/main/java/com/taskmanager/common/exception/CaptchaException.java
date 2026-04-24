package com.taskmanager.common.exception;

/**
 * 验证码异常
 *
 * @author taskmanager
 */
public class CaptchaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CaptchaException(String message) {
        super(message);
    }
}
