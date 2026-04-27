package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单 Mapper 接口
 *
 * @author taskmanager
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
