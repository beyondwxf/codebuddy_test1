package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单明细 Mapper 接口
 *
 * @author taskmanager
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {
}
