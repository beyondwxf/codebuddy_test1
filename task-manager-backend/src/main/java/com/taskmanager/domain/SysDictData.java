package com.taskmanager.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 字典数据实体（sys_dict_data 表）
 *
 * @author taskmanager
 */
@Data
@TableName("sys_dict_data")
public class SysDictData implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 字典编码 */
    @TableId(type = IdType.AUTO)
    private Long dictCode;

    /** 字典排序 */
    private Integer dictSort;

    /** 字典标签（展示名称） */
    private String dictLabel;

    /** 字典键值（实际值） */
    private String dictValue;

    /** 字典类型 */
    private String dictType;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认（Y是 N否） */
    private String isDefault;

    /** 状态（0正常 1停用） */
    private String status;

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
}
