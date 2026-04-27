package com.taskmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.common.Result;
import com.taskmanager.domain.Cart;
import com.taskmanager.domain.Order;
import com.taskmanager.domain.OrderItem;
import com.taskmanager.domain.Product;
import com.taskmanager.domain.ProductInventory;
import com.taskmanager.mapper.CartMapper;
import com.taskmanager.mapper.OrderItemMapper;
import com.taskmanager.mapper.OrderMapper;
import com.taskmanager.mapper.ProductInventoryMapper;
import com.taskmanager.mapper.ProductMapper;
import com.taskmanager.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 订单控制器
 * 提供订单的提交、查询、取消接口，需要登录后访问
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/shop/order")
public class OrderController {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private ProductInventoryMapper productInventoryMapper;

    /**
     * 提交订单
     */
    @PostMapping("/submit")
    @Transactional(rollbackFor = Exception.class)
    public Result<Long> submit(@RequestBody OrderSubmitDTO dto) {
        Long userId = getCurrentUserId();

        // 1. 验证 cartIds 不为空
        List<Long> cartIds = dto.getCartIds();
        if (cartIds == null || cartIds.isEmpty()) {
            return Result.error("购物车不能为空");
        }

        // 2. 查询购物车项并验证属于当前用户
        List<Cart> cartList = cartMapper.selectBatchIds(cartIds);
        if (cartList.size() != cartIds.size()) {
            return Result.error("购物车项不存在");
        }
        for (Cart cart : cartList) {
            if (!cart.getUserId().equals(userId)) {
                return Result.error("购物车项不存在或无权操作");
            }
        }

        // 3. 生成订单号
        String orderNo = generateOrderNo();

        // 4. 构建订单明细并计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            if (product == null) {
                throw new RuntimeException("商品不存在");
            }

            BigDecimal salePrice = product.getSalePrice() != null ? product.getSalePrice() : BigDecimal.ZERO;
            BigDecimal subtotal = salePrice.multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(product.getProductName());
            item.setSalePrice(salePrice);
            item.setQuantity(cart.getQuantity());
            item.setSubtotal(subtotal);
            orderItems.add(item);

            // 5. 扣减库存（取第一个有库存的仓库，并发安全）
            LambdaQueryWrapper<ProductInventory> inventoryQuery = new LambdaQueryWrapper<>();
            inventoryQuery.eq(ProductInventory::getProductId, cart.getProductId())
                    .eq(ProductInventory::getDelFlag, "0")
                    .gt(ProductInventory::getStockQuantity, 0)
                    .orderByAsc(ProductInventory::getId)
                    .last("LIMIT 1");
            ProductInventory inventory = productInventoryMapper.selectOne(inventoryQuery);

            if (inventory == null) {
                throw new RuntimeException("商品【" + product.getProductName() + "】库存不足");
            }

            LambdaUpdateWrapper<ProductInventory> inventoryUpdate = new LambdaUpdateWrapper<>();
            inventoryUpdate.eq(ProductInventory::getId, inventory.getId())
                    .ge(ProductInventory::getStockQuantity, cart.getQuantity())
                    .setSql("stock_quantity = stock_quantity - " + cart.getQuantity());
            int affected = productInventoryMapper.update(null, inventoryUpdate);
            if (affected == 0) {
                throw new RuntimeException("商品【" + product.getProductName() + "】库存不足");
            }
        }

        // 6. 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setReceiverName(dto.getReceiverName());
        order.setReceiverPhone(dto.getReceiverPhone());
        order.setReceiverAddress(dto.getReceiverAddress());
        order.setRemark(dto.getRemark());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 7. 插入订单明细
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getOrderId());
            orderItemMapper.insert(item);
        }

        // 8. 删除已下单的购物车项
        cartMapper.deleteBatchIds(cartIds);

        return Result.success(order.getOrderId());
    }

    /**
     * 当前用户订单列表
     */
    @GetMapping("/list")
    public Result<Page<Order>> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                    @RequestParam(defaultValue = "10") Integer pageSize) {
        Long userId = getCurrentUserId();
        Page<Order> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);
        Page<Order> result = orderMapper.selectPage(page, wrapper);
        return Result.success(result);
    }

    /**
     * 订单详情
     */
    @GetMapping("/{orderId}")
    public Result<Order> detail(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在或无权查看");
        }

        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);
        order.setOrderItems(items);

        return Result.success(order);
    }

    /**
     * 取消订单
     */
    @PutMapping("/cancel/{orderId}")
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> cancel(@PathVariable Long orderId) {
        Long userId = getCurrentUserId();
        Order order = orderMapper.selectById(orderId);
        if (order == null || !order.getUserId().equals(userId)) {
            return Result.error("订单不存在或无权操作");
        }

        if (order.getStatus() != 0) {
            return Result.error("只有待付款订单才能取消");
        }

        // 更新状态为已取消
        order.setStatus(4);
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.updateById(order);

        // 恢复库存
        LambdaQueryWrapper<OrderItem> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OrderItem::getOrderId, orderId);
        List<OrderItem> items = orderItemMapper.selectList(wrapper);

        for (OrderItem item : items) {
            LambdaQueryWrapper<ProductInventory> inventoryQuery = new LambdaQueryWrapper<>();
            inventoryQuery.eq(ProductInventory::getProductId, item.getProductId())
                    .eq(ProductInventory::getDelFlag, "0")
                    .orderByAsc(ProductInventory::getId)
                    .last("LIMIT 1");
            ProductInventory inventory = productInventoryMapper.selectOne(inventoryQuery);

            if (inventory != null) {
                LambdaUpdateWrapper<ProductInventory> inventoryUpdate = new LambdaUpdateWrapper<>();
                inventoryUpdate.eq(ProductInventory::getId, inventory.getId())
                        .setSql("stock_quantity = stock_quantity + " + item.getQuantity());
                productInventoryMapper.update(null, inventoryUpdate);
            }
        }

        return Result.success();
    }

    /**
     * 生成订单号：yyyyMMddHHmmss + 6位随机数
     */
    private String generateOrderNo() {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String randomStr = String.format("%06d", new Random().nextInt(1000000));
        return dateStr + randomStr;
    }

    /**
     * 从 SecurityContext 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        return loginUser.getUserId();
    }

    /**
     * 订单提交请求DTO
     */
    public static class OrderSubmitDTO {
        private List<Long> cartIds;
        private String receiverName;
        private String receiverPhone;
        private String receiverAddress;
        private String remark;

        public List<Long> getCartIds() {
            return cartIds;
        }

        public void setCartIds(List<Long> cartIds) {
            this.cartIds = cartIds;
        }

        public String getReceiverName() {
            return receiverName;
        }

        public void setReceiverName(String receiverName) {
            this.receiverName = receiverName;
        }

        public String getReceiverPhone() {
            return receiverPhone;
        }

        public void setReceiverPhone(String receiverPhone) {
            this.receiverPhone = receiverPhone;
        }

        public String getReceiverAddress() {
            return receiverAddress;
        }

        public void setReceiverAddress(String receiverAddress) {
            this.receiverAddress = receiverAddress;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }
    }
}
