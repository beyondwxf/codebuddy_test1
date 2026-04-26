package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.ProductInventory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 商品库存 Mapper 接口
 *
 * @author taskmanager
 */
public interface ProductInventoryMapper extends BaseMapper<ProductInventory> {

    /**
     * 根据商品ID查询库存列表（含仓库名称）
     */
    List<ProductInventory> selectByProductId(@Param("productId") Long productId);

    /**
     * 逻辑删除商品的所有库存记录
     */
    int logicDeleteByProductId(@Param("productId") Long productId);
}
