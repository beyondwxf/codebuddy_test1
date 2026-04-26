package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.domain.Warehouse;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 仓库 Mapper 接口
 *
 * @author taskmanager
 */
public interface WarehouseMapper extends BaseMapper<Warehouse> {

    /**
     * 分页查询仓库列表（支持多条件筛选）
     *
     * @param page           分页参数
     * @param warehouseName  仓库名称（模糊查询）
     * @param warehouseCode  仓库编码（模糊查询）
     * @param provinceList   省份列表（多选精确匹配）
     * @param warehouseType  仓库类型
     * @param status         状态
     * @return 分页结果
     */
    Page<Warehouse> selectWarehouseList(Page<Warehouse> page,
                                         @Param("warehouseName") String warehouseName,
                                         @Param("warehouseCode") String warehouseCode,
                                         @Param("provinceList") List<String> provinceList,
                                         @Param("warehouseType") String warehouseType,
                                         @Param("status") String status);

    /**
     * 查询所有正常状态的仓库（下拉选择用）
     */
    List<Warehouse> selectWarehouseAll();
}
