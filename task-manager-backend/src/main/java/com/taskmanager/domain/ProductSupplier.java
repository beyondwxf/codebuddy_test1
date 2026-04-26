package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 商品供应商关联实体（wms_product_supplier 表）
 *
 * @author taskmanager
 */
@Data
@TableName("wms_product_supplier")
public class ProductSupplier implements Serializable {

    private static final long serialVersionUID = 1L;

    /** ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 商品ID */
    private Long productId;

    /** 供应商ID */
    private Long supplierId;

    /** 供应商商品编码 */
    private String supplierSkuCode;

    /** 供应商报价 */
    private BigDecimal supplierPrice;

    /** 交货周期（天） */
    private Integer leadTime;

    /** 是否默认供应商（0否 1是） */
    private String isDefault;

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

    /** 供应商名称（关联查询） */
    private String supplierName;

    /** 公司名称（关联查询） */
    private String companyName;
}
