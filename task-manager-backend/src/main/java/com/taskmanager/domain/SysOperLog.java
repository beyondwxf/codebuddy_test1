package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志实体（sys_oper_log 表）
 *
 * @author taskmanager
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 日志主键 */
    @TableId(type = IdType.AUTO)
    private Long operId;

    /** 模块标题 */
    private String module;

    /** 业务类型（0其它 1新增 2修改 3删除） */
    private Integer businessType;

    /** 方法名称 */
    private String method;

    /** 请求方式（GET/POST/PUT/DELETE） */
    private String requestMethod;

    /** 操作类别（0其它 1后台用户） */
    private Integer operatorType;

    /** 操作人员 */
    private String operName;

    /** 部门名称 */
    private String deptName;

    /** 请求URL */
    private String operUrl;

    /** 主机地址（IP） */
    private String operIp;

    /** 操作地点 */
    private String operLocation;

    /** 请求参数（JSON 格式） */
    private String operParam;

    /** 返回参数（JSON 格式） */
    private String jsonResult;

    /** 操作状态（0正常 1异常） */
    private Integer status;

    /** 错误消息 */
    private String errorMsg;

    /** 操作时间 */
    private Date operTime;

    /** 消耗时间（毫秒） */
    private Long costTime;
}
