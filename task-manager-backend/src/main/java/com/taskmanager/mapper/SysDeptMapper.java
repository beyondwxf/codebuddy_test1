package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.domain.SysDept;

import java.util.List;

/**
 * 部门 Mapper 接口
 *
 * @author taskmanager
 */
public interface SysDeptMapper extends BaseMapper<SysDept> {

    /** 查询全部部门树（构建部门选择器） */
    List<SysDept> selectDeptTreeAll();

    /** 根据部门ID查询子部门数量（删除前检查用） */
    int selectChildrenCountByDeptId(Long deptId);
}
