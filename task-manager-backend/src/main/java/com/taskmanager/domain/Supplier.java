package com.taskmanager.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 供应商信息实体（sys_supplier 表）
 *
 * @author taskmanager
 */
@Data
@TableName("sys_supplier")
public class Supplier implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 供应商ID */
    @TableId(type = IdType.AUTO)
    private Long supplierId;

    /** 公司名称 */
    @ExcelProperty("公司名称")
    private String companyName;

    /** 省份 */
    @ExcelProperty("省份")
    private String province;

    /** 联系人 */
    @ExcelProperty("联系人")
    private String contactPerson;

    /** 电话1 */
    @ExcelProperty("电话1")
    private String phone1;

    /** 电话2 */
    @ExcelProperty("电话2")
    private String phone2;

    /** 电话3 */
    @ExcelProperty("电话3")
    private String phone3;

    /** 电话4 */
    @ExcelProperty("电话4")
    private String phone4;

    /** 联系状态（0未联系 1已加微信 2未接 3空号 4已下单） */
    @ExcelProperty("联系状态")
    private String contactStatus;

    /** 品类（逗号分隔） */
    @ExcelProperty("品类")
    private String category;

    /** 详细地址 */
    @ExcelProperty("详细地址")
    private String address;

    /** 删除标志（0存在 2删除） */
    private String delFlag;

    /** 创建者 */
    private Long createBy;

    /** 创建时间 */
    private Date createTime;

    /** 更新者 */
    private Long updateBy;

    /** 更新时间 */
    private Date updateTime;

    /** 备注 */
    @ExcelProperty("备注")
    private String remark;
}
