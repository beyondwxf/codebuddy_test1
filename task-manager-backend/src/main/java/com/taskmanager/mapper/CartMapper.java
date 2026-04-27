package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 购物车 Mapper 接口
 *
 * @author taskmanager
 */
@Mapper
public interface CartMapper extends BaseMapper<Cart> {

    /**
     * 查询用户购物车列表（含商品信息）
     *
     * @param userId 用户ID
     * @return 购物车列表
     */
    List<Cart> selectCartListByUserId(@Param("userId") Long userId);
}
