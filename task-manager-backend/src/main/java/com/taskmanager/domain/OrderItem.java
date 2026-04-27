package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 订单明细实体（ecommerce_order_item 表）
 *
 * @author taskmanager
 */
@Data
@TableName("ecommerce_order_item")
public class OrderItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 明细ID */
    @TableId(type = IdType.AUTO)
    private Long itemId;

    /** 订单ID */
    private Long orderId;

    /** 商品ID */
    private Long productId;

    /** 商品名称 */
    private String productName;

    /** 售价 */
    private BigDecimal salePrice;

    /** 数量 */
    private Integer quantity;

    /** 小计金额 */
    private BigDecimal subtotal;
}
