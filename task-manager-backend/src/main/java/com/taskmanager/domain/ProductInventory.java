package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 商品库存实体（wms_product_inventory 表）
 *
 * @author taskmanager
 */
@Data
@TableName("wms_product_inventory")
public class ProductInventory implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 仓库ID */
    private Long warehouseId;

    /** 库存数量 */
    private Integer stockQuantity;

    /** 预警数量 */
    private Integer warningQuantity;

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
    private String remark;

    // ========== 非数据库字段 ==========

    /** 仓库名称（关联查询） */
    @TableField(exist = false)
    private String warehouseName;

    /** 仓库编码（关联查询） */
    @TableField(exist = false)
    private String warehouseCode;
}
