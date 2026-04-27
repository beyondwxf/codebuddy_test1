package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 购物车实体（ecommerce_cart 表）
 *
 * @author taskmanager
 */
@Data
@TableName("ecommerce_cart")
public class Cart implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 购物车ID */
    @TableId(type = IdType.AUTO)
    private Long cartId;

    /** 用户ID */
    private Long userId;

    /** 商品ID */
    private Long productId;

    /** 数量 */
    private Integer quantity;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    // ========== 非数据库字段（用于返回前端） ==========

    /** 商品名称 */
    @TableField(exist = false)
    private String productName;

    /** 预览图URL */
    @TableField(exist = false)
    private String previewImage;

    /** 售价 */
    @TableField(exist = false)
    private BigDecimal salePrice;

    /** 单位 */
    @TableField(exist = false)
    private String unit;
}
