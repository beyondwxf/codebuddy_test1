package com.taskmanager.common.constant;

/**
 * 全局常量定义
 *
 * @author taskmanager
 */
public final class Constants {

    /** 成功标记 */
    public static final int SUCCESS = 200;

    /** 操作失败标记 */
    public static final int FAIL = 500;

    /** 未认证状态码 */
    public static final int UNAUTHORIZED = 401;

    /** 无权限状态码 */
    public static final int FORBIDDEN = 403;

    /** 验证码 Redis Key 前缀 */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /** 验证码有效期（分钟） */
    public static final long CAPTCHA_EXPIRATION = 5;

    /** 登录用户 Redis Key */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /** Token 有效期（默认2小时，由配置文件控制） */

    /** 防重提交 Redis Key 前缀 */
    public static final String REPEAT_SUBMIT_KEY = "repeat_submit:";

    private Constants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}
