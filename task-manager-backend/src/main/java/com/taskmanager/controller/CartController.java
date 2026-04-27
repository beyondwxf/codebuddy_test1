package com.taskmanager.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.taskmanager.common.Result;
import com.taskmanager.domain.Cart;
import com.taskmanager.domain.Product;
import com.taskmanager.mapper.CartMapper;
import com.taskmanager.mapper.ProductMapper;
import com.taskmanager.security.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 购物车控制器
 * 提供购物车的增删改查接口，需要登录后访问
 *
 * @author taskmanager
 */
@RestController
@RequestMapping("/api/shop/cart")
public class CartController {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    /**
     * 获取当前用户购物车列表
     */
    @GetMapping("/list")
    public Result<List<Cart>> list() {
        Long userId = getCurrentUserId();
        List<Cart> list = cartMapper.selectCartListByUserId(userId);
        return Result.success(list);
    }

    /**
     * 添加到购物车
     * 如果该商品已在购物车中，则累加数量；否则新增记录
     */
    @PostMapping("/add")
    public Result<Void> add(@RequestBody Cart cart) {
        Long userId = getCurrentUserId();
        Long productId = cart.getProductId();
        int quantity = cart.getQuantity() != null ? cart.getQuantity() : 1;

        // 验证商品存在且上架
        Product product = productMapper.selectById(productId);
        if (product == null || !"0".equals(product.getStatus()) || !"0".equals(product.getDelFlag())) {
            return Result.error("商品不存在或已下架");
        }

        // 检查该用户是否已有此商品在购物车中
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart existingCart = cartMapper.selectOne(wrapper);

        if (existingCart != null) {
            // 已存在：累加数量
            existingCart.setQuantity(existingCart.getQuantity() + quantity);
            cartMapper.updateById(existingCart);
        } else {
            // 不存在：新增购物车记录
            cart.setUserId(userId);
            cart.setQuantity(quantity);
            cartMapper.insert(cart);
        }

        return Result.success();
    }

    /**
     * 修改购物车数量
     * 数量 <= 0 时删除该项
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody Cart cart) {
        Long userId = getCurrentUserId();
        Long cartId = cart.getCartId();

        // 验证该购物车项属于当前用户
        Cart existingCart = cartMapper.selectById(cartId);
        if (existingCart == null || !existingCart.getUserId().equals(userId)) {
            return Result.error("购物车项不存在或无权操作");
        }

        if (cart.getQuantity() <= 0) {
            // 数量 <= 0 则删除该项
            cartMapper.deleteById(cartId);
        } else {
            // 更新数量
            existingCart.setQuantity(cart.getQuantity());
            cartMapper.updateById(existingCart);
        }

        return Result.success();
    }

    /**
     * 删除购物车项
     * cartIds 为逗号分隔的ID字符串，只删除属于当前用户的购物车项
     */
    @DeleteMapping("/{cartIds}")
    public Result<Void> remove(@PathVariable String cartIds) {
        Long userId = getCurrentUserId();
        String[] idArray = cartIds.split(",");
        for (String idStr : idArray) {
            Long cartId = Long.parseLong(idStr.trim());
            // 只删除属于当前用户的购物车项
            Cart cart = cartMapper.selectById(cartId);
            if (cart != null && cart.getUserId().equals(userId)) {
                cartMapper.deleteById(cartId);
            }
        }
        return Result.success();
    }

    /**
     * 从 SecurityContext 获取当前登录用户ID
     */
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) auth.getPrincipal();
        return loginUser.getUserId();
    }
}
