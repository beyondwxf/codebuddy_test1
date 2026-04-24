package com.taskmanager.common.enums;

/**
 * 业务操作类型枚举
 *
 * @author taskmanager
 */
public enum BusinessTypeEnum {

    /** 其它 */
    OTHER(0, "其它"),

    /** 新增 */
    INSERT(1, "新增"),

    /** 修改 */
    UPDATE(2, "修改"),

    /** 删除 */
    DELETE(3, "删除"),

    /** 授权 */
    GRANT(4, "授权"),

    /** 导出 */
    EXPORT(5, "导出"),

    /** 导入 */
    IMPORT(6, "导入"),

    /** 强退 */
    FORCE(7, "强退"),

    /** 生成代码 */
    GENCODE(8, "生成代码"),

    /** 清空数据 */
    CLEAN(9, "清空数据");

    private final int code;
    private final String description;

    BusinessTypeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
