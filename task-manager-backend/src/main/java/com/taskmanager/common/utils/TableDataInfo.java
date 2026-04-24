package com.taskmanager.common.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 表格分页数据封装对象
 *
 * @author taskmanager
 */
@Data
public class TableDataInfo<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 总记录数 */
    private long total;

    /** 当前页数据列表 */
    private List<T> rows;

    /** 当前页码 */
    private int pageNum;

    /** 每页大小 */
    private int pageSize;

    /** 总页码 */
    private int pages;

    /**
     * 从 MyBatis-Plus Page 对象构建
     */
    public static <T> TableDataInfo<T> build(Page<T> page) {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setTotal(page.getTotal());
        rspData.setRows(page.getRecords());
        rspData.setPageNum((int) page.getCurrent());
        rspData.setPageSize((int) page.getSize());
        rspData.setPages((int) page.getPages());
        return rspData;
    }

    /**
     * 空表格数据
     */
    public static <T> TableDataInfo<T> empty() {
        TableDataInfo<T> rspData = new TableDataInfo<>();
        rspData.setTotal(0);
        rspData.setRows(new ArrayList<>());
        rspData.setPageNum(0);
        rspData.setPageSize(10);
        rspData.setPages(0);
        return rspData;
    }
}
