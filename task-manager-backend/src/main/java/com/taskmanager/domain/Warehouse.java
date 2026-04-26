package com.taskmanager.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库信息实体（wms_warehouse 表）
 *
 * @author taskmanager
 */
@Data
@TableName("wms_warehouse")
public class Warehouse implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 仓库ID */
    @TableId(type = IdType.AUTO)
    private Long warehouseId;

    /** 仓库名称 */
    @ExcelProperty("仓库名称")
    private String warehouseName;

    /** 仓库编码 */
    @ExcelProperty("仓库编码")
    private String warehouseCode;

    /** 省份 */
    @ExcelProperty("省份")
    private String province;

    /** 详细地址 */
    @ExcelProperty("详细地址")
    private String address;

    /** 仓库类型（0自有 1租赁） */
    @ExcelProperty("仓库类型")
    private String warehouseType;

    /** 状态（0正常 1停用） */
    @ExcelProperty("状态")
    private String status;

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
