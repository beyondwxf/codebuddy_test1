package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.domain.Supplier;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商 Mapper 接口
 *
 * @author taskmanager
 */
public interface SupplierMapper extends BaseMapper<Supplier> {

    /**
     * 分页查询供应商列表（支持多条件筛选）
     *
     * @param page          分页参数
     * @param companyName   公司名称（模糊查询）
     * @param provinceList  省份列表（多选精确匹配）
     * @param contactPerson 联系人（模糊查询）
     * @param categoryList  品类列表（多选，FIND_IN_SET匹配）
     * @param contactStatus 联系状态（精确匹配）
     * @return 分页结果
     */
    Page<Supplier> selectSupplierList(Page<Supplier> page,
                                      @Param("companyName") String companyName,
                                      @Param("provinceList") List<String> provinceList,
                                      @Param("contactPerson") String contactPerson,
                                      @Param("categoryList") List<String> categoryList,
                                      @Param("contactStatus") String contactStatus);
}
