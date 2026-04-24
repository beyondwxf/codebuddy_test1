package com.taskmanager.aspect;

import com.taskmanager.common.annotation.Log;
import com.taskmanager.common.enums.BusinessTypeEnum;
import com.taskmanager.domain.SysOperLog;
import com.taskmanager.mapper.SysOperLogMapper;
import com.taskmanager.security.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 操作日志切面
 * 拦截标注了 @Log 注解的方法，自动记录操作日志到 sys_oper_log 表
 *
 * @author taskmanager
 */
@Aspect
@Component
public class LogAspect {

    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private final SysOperLogMapper operLogMapper;
    private final ObjectMapper objectMapper;

    public LogAspect(SysOperLogMapper operLogMapper, ObjectMapper objectMapper) {
        this.operLogMapper = operLogMapper;
        this.objectMapper = objectMapper;
    }

    /**
     * 环绕通知：在方法执行前后记录操作日志
     */
    @Around("@annotation(controllerLog)")
    public Object doAround(ProceedingJoinPoint joinPoint, Log controllerLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        SysOperLog operLog = new SysOperLog();
        try {
            operLog.setModule(controllerLog.title());
            operLog.setBusinessType(controllerLog.businessType().getCode());
            // 获取请求信息
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                operLog.setOperIp(getClientIp(request));
                operLog.setOperUrl(request.getRequestURI());
                operLog.setRequestMethod(request.getMethod());
                // 设置操作人
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.getPrincipal() instanceof LoginUser loginUser) {
                    operLog.setOperName(loginUser.getUser().getNickName());
                }
            }
            // 保存请求参数（过滤敏感字段）
            if (controllerLog.isSaveRequestData()) {
                setRequestMessage(joinPoint, operLog);
            }
            // 执行目标方法
            Object result = joinPoint.proceed();
            long costTime = System.currentTimeMillis() - startTime;
            operLog.setCostTime(costTime);
            operLog.setStatus(0); // 正常
            if (controllerLog.isSaveResponseData()) {
                operLog.setJsonResult(toJsonString(result));
            } else {
                operLog.setJsonResult(null);
            }
            return result;
        } catch (Exception e) {
            long costTime = System.currentTimeMillis() - startTime;
            operLog.setCostTime(costTime);
            operLog.setStatus(1); // 异常
            operLog.setErrorMsg(e.getMessage() != null && e.getMessage().length() > 2000
                    ? e.getMessage().substring(0, 2000) : e.getMessage());
            throw e;
        } finally {
            // 写入数据库
            try {
                operLog.setOperTime(new java.util.Date());
                operLog.setOperatorType(1);
                operLogMapper.insert(operLog);
            } catch (Exception ex) {
                logger.warn("操作日志写入失败: {}", ex.getMessage());
            }
        }
    }

    /** 设置请求参数（过滤 password 等敏感信息） */
    private void setRequestMessage(ProceedingJoinPoint joinPoint, SysOperLog operLog) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                String params = toJsonString(args[0]);
                params = params.replaceAll("(?i)(\"?password\"?:\\s*\"?[^\"]*\"?)", "\"password\":\"******\"");
                operLog.setOperParam(params);
            }
        } catch (Exception e) {
            operLog.setOperParam("参数解析失败");
        }
    }

    /** 对象转 JSON 字符串 */
    private String toJsonString(Object obj) {
        try {
            return obj == null ? "" : objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "";
        }
    }

    /** 获取客户端真实 IP */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
