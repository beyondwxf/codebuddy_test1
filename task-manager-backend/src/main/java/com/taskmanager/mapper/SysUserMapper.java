package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.taskmanager.domain.SysUser;
import org.apache.ibatis.annotations.Param;

/**
 * 用户 Mapper 接口
 *
 * @author taskmanager
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户信息（登录用）
     *
     * @param userName 用户账号
     * @return 用户实体，不存在返回 null
     */
    SysUser selectByUserName(@Param("userName") String userName);

    /**
     * 分页查询用户列表（支持多条件筛选）
     *
     * @param page        分页参数
     * @param userName    用户名（模糊查询）
     * @param phonenumber 手机号（模糊查询）
     * @param status      状态（精确匹配）
     * @param deptId      部门ID（精确匹配）
     * @return 分页结果
     */
    Page<SysUser> selectUserList(Page<SysUser> page,
                                 @Param("userName") String userName,
                                 @Param("phonenumber") String phonenumber,
                                 @Param("status") String status,
                                 @Param("deptId") Long deptId);
}
