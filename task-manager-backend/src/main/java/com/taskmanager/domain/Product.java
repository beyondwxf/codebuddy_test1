package com.taskmanager.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品信息实体（wms_product 表）
 *
 * @author taskmanager
 */
@Data
@TableName("wms_product")
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 商品ID */
    @TableId(type = IdType.AUTO)
    private Long productId;

    /** 商品名称 */
    @ExcelProperty("商品名称")
    private String productName;

    /** SKU编码 */
    @ExcelProperty("SKU编码")
    private String skuCode;

    /** 商品简介 */
    @ExcelProperty("商品简介")
    private String description;

    /** 预览图URL */
    private String previewImage;

    /** 移动端宣传页内容（富文本） */
    private String mobileContent;

    /** 售价 */
    @ExcelProperty("售价")
    private BigDecimal salePrice;

    /** 进货价 */
    @ExcelProperty("进货价")
    private BigDecimal purchasePrice;

    /** 单位 */
    @ExcelProperty("单位")
    private String unit;

    /** 状态（0上架 1下架） */
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

    // ========== 非数据库字段 ==========

    /** 商品供应商关联列表（非数据库字段） */
    @TableField(exist = false)
    private List<ProductSupplier> supplierList;

    /** 商品库存列表（非数据库字段） */
    @TableField(exist = false)
    private List<ProductInventory> inventoryList;
}
