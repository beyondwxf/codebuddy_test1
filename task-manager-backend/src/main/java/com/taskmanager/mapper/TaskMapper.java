package com.taskmanager.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.taskmanager.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 任务 Mapper 接口
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {

    /**
     * 分页查询任务列表
     *
     * @param userId   用户ID
     * @param status   状态筛选（可选）
     * @param keyword  关键词搜索（可选）
     * @return 任务列表
     */
    List<Task> selectTaskPage(@Param("userId") Long userId,
                              @Param("status") Integer status,
                              @Param("keyword") String keyword);

    /**
     * 根据用户ID查询所有任务
     *
     * @param userId 用户ID
     * @return 任务列表
     */
    List<Task> selectByUserId(@Param("userId") Long userId);

    /**
     * 更新任务状态
     *
     * @param id        任务ID
     * @param completed 是否完成
     * @param userId    用户ID（权限校验）
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id,
                     @Param("completed") Boolean completed,
                     @Param("userId") Long userId);

    /**
     * 根据ID和用户ID查询任务
     *
     * @param id     任务ID
     * @param userId 用户ID
     * @return 任务对象
     */
    Task selectByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);
}
