-- =============================================
-- 若依后台管理系统 - 数据库初始化脚本
-- 数据库：ruoyi_admin
-- =============================================

SET NAMES utf8mb4;
CREATE DATABASE IF NOT EXISTS `ruoyi_admin` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `ruoyi_admin`;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
    `user_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `dept_id`     BIGINT       DEFAULT NULL COMMENT '部门ID',
    `user_name`   VARCHAR(30)  NOT NULL COMMENT '用户账号',
    `nick_name`   VARCHAR(30)  NOT NULL COMMENT '用户昵称',
    `user_type`   VARCHAR(2)   DEFAULT '00' COMMENT '用户类型（00系统用户）',
    `email`       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    `phonenumber` VARCHAR(11)  DEFAULT NULL COMMENT '手机号码',
    `sex`         CHAR(1)      DEFAULT '0' COMMENT '性别（0男 1女 2未知）',
    `avatar`      VARCHAR(100) DEFAULT NULL COMMENT '头像地址',
    `password`    VARCHAR(100) DEFAULT '' COMMENT '密码（加密存储）',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '帐号状态（0正常 1停用）',
    `del_flag`    CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `login_ip`    VARCHAR(128) DEFAULT NULL COMMENT '最后登录IP',
    `login_date`  DATETIME     DEFAULT NULL COMMENT '最后登录时间',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='用户信息表';

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
    `role_id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name`            VARCHAR(30)  NOT NULL COMMENT '角色名称',
    `role_key`             VARCHAR(100) NOT NULL COMMENT '角色权限字符串',
    `role_sort`            INT          NOT NULL DEFAULT 0 COMMENT '显示顺序',
    `data_scope`           CHAR(1)      DEFAULT '1' COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下 5仅本人）',
    `menu_check_strictly`  TINYINT(1)   DEFAULT 1 COMMENT '菜单树选择项是否关联显示',
    `dept_check_strictly`  TINYINT(1)   DEFAULT 1 COMMENT '部门树选择项是否关联显示',
    `status`               CHAR(1)      DEFAULT '0' COMMENT '角色状态（0正常 1停用）',
    `del_flag`             CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `create_by`            BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`            BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time`          DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`               VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='角色信息表';

-- ----------------------------
-- 3. 菜单权限表
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
    `menu_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name`   VARCHAR(50)  NOT NULL COMMENT '菜单名称',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父菜单ID',
    `order_num`   INT          DEFAULT 0 COMMENT '显示顺序',
    `path`        VARCHAR(200) DEFAULT '' COMMENT '路由地址',
    `component`   VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    `query`       VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    `route_name`  VARCHAR(50)  DEFAULT '' COMMENT '路由名称',
    `is_frame`    INT          DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    `is_cache`    INT          DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    `menu_type`   CHAR(1)      DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    `visible`     CHAR(1)      DEFAULT 0 COMMENT '显示状态（0显示 1隐藏）',
    `status`      CHAR(1)      DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
    `perms`       VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    `icon`        VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT '' COMMENT '备注',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2000 COMMENT='菜单权限表';

-- ----------------------------
-- 4. 部门表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
    `dept_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门id',
    `parent_id`   BIGINT       DEFAULT 0 COMMENT '父部门id',
    `ancestors`   VARCHAR(50)  DEFAULT '' COMMENT '祖级列表',
    `dept_name`   VARCHAR(30)  DEFAULT '' COMMENT '部门名称',
    `order_num`   INT          DEFAULT 0 COMMENT '显示顺序',
    `leader`      VARCHAR(20)  DEFAULT NULL COMMENT '负责人',
    `phone`       VARCHAR(11)  DEFAULT NULL COMMENT '联系电话',
    `email`       VARCHAR(50)  DEFAULT NULL COMMENT '邮箱',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
    `del_flag`    CHAR(1)      DEFAULT '0' COMMENT '删除标志（0存在 2删除）',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='部门表';

-- ----------------------------
-- 5. 用户角色关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_id`, `role_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB COMMENT='用户和角色关联表';

-- ----------------------------
-- 6. 角色菜单关联表
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
    `role_id` BIGINT NOT NULL COMMENT '角色ID',
    `menu_id` BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_menu_id` (`menu_id`)
) ENGINE=InnoDB COMMENT='角色和菜单关联表';

-- ----------------------------
-- 7. 字典类型表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
    `dict_id`     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    `dict_name`   VARCHAR(100) DEFAULT '' COMMENT '字典名称',
    `dict_type`   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_id`),
    UNIQUE KEY `uk_dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='字典类型表';

-- ----------------------------
-- 8. 字典数据表
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
    `dict_code`   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    `dict_sort`   INT          DEFAULT 0 COMMENT '字典排序',
    `dict_label`  VARCHAR(100) DEFAULT '' COMMENT '字典标签',
    `dict_value`  VARCHAR(100) DEFAULT '' COMMENT '字典键值',
    `dict_type`   VARCHAR(100) DEFAULT '' COMMENT '字典类型',
    `css_class`   VARCHAR(100) DEFAULT NULL COMMENT '样式属性',
    `list_class`  VARCHAR(100) DEFAULT NULL COMMENT '表格回显样式',
    `is_default`  CHAR(1)      DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
    `status`      CHAR(1)      DEFAULT '0' COMMENT '状态（0正常 1停用）',
    `create_by`   BIGINT       DEFAULT NULL COMMENT '创建者',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by`   BIGINT       DEFAULT NULL COMMENT '更新者',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark`      VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`dict_code`),
    KEY `idx_dict_type` (`dict_type`)
) ENGINE=InnoDB COMMENT='字典数据表';

-- ----------------------------
-- 9. 操作日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
    `oper_id`        BIGINT        NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    `module`         VARCHAR(50)   DEFAULT '' COMMENT '模块标题',
    `business_type`  INT           DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    `method`         VARCHAR(100)  DEFAULT '' COMMENT '方法名称',
    `request_method` VARCHAR(10)   DEFAULT '' COMMENT '请求方式',
    `operator_type`  INT           DEFAULT 0 COMMENT '操作类别（0其它 1后台用户）',
    `oper_name`      VARCHAR(50)   DEFAULT '' COMMENT '操作人员',
    `dept_name`      VARCHAR(50)   DEFAULT '' COMMENT '部门名称',
    `oper_url`       VARCHAR(255)  DEFAULT '' COMMENT '请求URL',
    `oper_ip`        VARCHAR(128)  DEFAULT '' COMMENT '主机地址',
    `oper_location`  VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    `oper_param`     LONGTEXT      DEFAULT NULL COMMENT '请求参数',
    `json_result`    LONGTEXT      DEFAULT NULL COMMENT '返回参数',
    `status`         INT           DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    `error_msg`      VARCHAR(2000) DEFAULT '' COMMENT '错误消息',
    `oper_time`      DATETIME      DEFAULT NULL COMMENT '操作时间',
    `cost_time`      BIGINT        DEFAULT 0 COMMENT '消耗时间（毫秒）',
    PRIMARY KEY (`oper_id`),
    KEY `idx_oper_time` (`oper_time`),
    KEY `idx_oper_name` (`oper_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='操作日志记录';

-- ----------------------------
-- 10. 登录日志表
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
    `info_id`        BIGINT       NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    `user_name`      VARCHAR(50)  DEFAULT '' COMMENT '用户账号',
    `ipaddr`         VARCHAR(128) DEFAULT '' COMMENT '登录IP地址',
    `login_location` VARCHAR(255) DEFAULT '' COMMENT '登录地点',
    `browser`        VARCHAR(50)  DEFAULT '' COMMENT '浏览器类型',
    `os`             VARCHAR(50)  DEFAULT '' COMMENT '操作系统',
    `status`         CHAR(1)      DEFAULT 0 COMMENT '登录状态（0成功 1失败）',
    `msg`            VARCHAR(255) DEFAULT '' COMMENT '提示消息',
    `login_time`     DATETIME     DEFAULT NULL COMMENT '访问时间',
    PRIMARY KEY (`info_id`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_user_name` (`user_name`)
) ENGINE=InnoDB AUTO_INCREMENT=100 COMMENT='系统访问记录';

-- ============================================================
-- 初始化数据
-- ============================================================

-- 管理员：admin / admin（密码为BCrypt加密）
INSERT INTO `sys_user` VALUES (1, 103, 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '127.0.0.1', sysdate(), 1, sysdate(), NULL, NULL, '管理员');
INSERT INTO `sys_user` VALUES (2, 105, 'ry', '若依测试', '00', 'ry_test@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', sysdate(), 1, sysdate(), NULL, NULL, '测试员');

-- 部门数据
INSERT INTO `sys_dept` VALUES (100,  0,   '0',        '若依科技',  0, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (101,  100, '0,100',    '深圳总公司', 1, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (102,  100, '0,100',    '长沙分公司', 2, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (103,  101, '0,100,101', '研发部门',   1, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (104,  101, '0,100,101', '市场部门',   2, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (105,  101, '0,100,101', '测试部门',   3, '若依', '15888888888', 'ry@163.com', '0', '0', 1, sysdate(), NULL, NULL);

-- 角色数据
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'admin',  1, '1', 1, 1, '0', '0', 1, sysdate(), NULL, NULL, '超级管理员');
INSERT INTO `sys_role` VALUES ('2', '普通角色',   'common', 2, '2', 1, 1, '0', '0', 1, sysdate(), NULL, NULL, '普通角色');

-- 用户-角色关联
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2');

-- 一级目录
INSERT INTO `sys_menu` VALUES ('1', '系统管理', 0, 1, 'system', NULL, '', '', 1, 0, 'M', '0', '0', '', 'setting', 1, sysdate(), NULL, NULL, '系统管理目录');
INSERT INTO `sys_menu` VALUES ('2', '系统监控', 0, 2, 'monitor', NULL, '', '', 1, 0, 'M', '0', '0', '', 'monitor', 1, sysdate(), NULL, NULL, '系统监控目录');

-- 二级菜单 - 系统管理
INSERT INTO `sys_menu` VALUES ('101', '用户管理', 1, 1, 'user',       'system/user/index',        '', '', 1, 0, 'C', '0', '0', 'system:user:list',    'user',          1, sysdate(), NULL, NULL, '用户管理菜单');
INSERT INTO `sys_menu` VALUES ('102', '角色管理', 1, 2, 'role',       'system/role/index',        '', '', 1, 0, 'C', '0', '0', 'system:role:list',    'peoples',       1, sysdate(), NULL, NULL, '角色管理菜单');
INSERT INTO `sys_menu` VALUES ('103', '菜单管理', 1, 3, 'menu',       'system/menu/index',        '', '', 1, 0, 'C', '0', '0', 'system:menu:list',    'tree-table',    1, sysdate(), NULL, NULL, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES ('104', '部门管理', 1, 4, 'dept',       'system/dept/index',        '', '', 1, 0, 'C', '0', '0', 'system:dept:list',    'tree',          1, sysdate(), NULL, NULL, '部门管理菜单');
INSERT INTO `sys_menu` VALUES ('105', '字典管理', 1, 5, 'dict',       'system/dict/index',        '', '', 1, 0, 'C', '0', '0', 'system:dict:list',    'read',          1, sysdate(), NULL, NULL, '字典管理菜单');

-- 二级菜单 - 系统监控
INSERT INTO `sys_menu` VALUES ('201', '操作日志', 2, 1, 'operlog',     'monitor/operlog/index',    '', '', 1, 0, 'C', '0', '0', 'monitor:operlog:list', 'log',          1, sysdate(), NULL, NULL, '操作日志菜单');
INSERT INTO `sys_menu` VALUES ('202', '登录日志', 2, 2, 'logininfor', 'monitor/logininfor/index', '', '', 1, 0, 'C', '0', '0', 'monitor:logininfor:list', 'logininfor',   1, sysdate(), NULL, NULL, '登录日志菜单');

-- 用户管理按钮
INSERT INTO `sys_menu` VALUES ('1001', '用户查询', 101, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:query',    '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1002', '用户新增', 101, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:add',      '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1003', '用户修改', 101, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:edit',     '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1004', '用户删除', 101, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:remove',   '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1005', '重置密码', 101, 5, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:resetPwd','#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1006', '用户导出', 101, 6, '', '', '', '', 0, 0, 'F', '0', '0', 'system:user:export',  '#', 1, sysdate(), NULL, NULL, '');

-- 角色管理按钮
INSERT INTO `sys_menu` VALUES ('1007', '角色查询', 102, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'system:role:query',    '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1008', '角色新增', 102, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'system:role:add',      '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1009', '角色修改', 102, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'system:role:edit',     '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1010', '角色删除', 102, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'system:role:remove',  '#', 1, sysdate(), NULL, NULL, '');

-- 菜单管理按钮
INSERT INTO `sys_menu` VALUES ('1011', '菜单查询', 103, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'system:menu:query',    '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1012', '菜单新增', 103, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'system:menu:add',      '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1013', '菜单修改', 103, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'system:menu:edit',     '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1014', '菜单删除', 103, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'system:menu:remove',  '#', 1, sysdate(), NULL, NULL, '');

-- 部门管理按钮
INSERT INTO `sys_menu` VALUES ('1015', '部门查询', 104, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dept:query',    '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1016', '部门新增', 104, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dept:add',      '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1017', '部门修改', 104, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dept:edit',     '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1018', '部门删除', 104, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dept:remove',  '#', 1, sysdate(), NULL, NULL, '');

-- 字典管理按钮
INSERT INTO `sys_menu` VALUES ('1019', '字典查询', 105, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dict:query',    '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1020', '字典新增', 105, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dict:add',      '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1021', '字典修改', 105, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dict:edit',     '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1022', '字典删除', 105, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'system:dict:remove',  '#', 1, sysdate(), NULL, NULL, '');

-- 操作日志按钮
INSERT INTO `sys_menu` VALUES ('1025', '操作查询', 201, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'monitor:operlog:query',  '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1026', '操作删除', 201, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'monitor:operlog:remove', '#', 1, sysdate(), NULL, NULL, '');

-- 登录日志按钮
INSERT INTO `sys_menu` VALUES ('1028', '登录查询', 202, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'monitor:logininfor:query',  '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO `sys_menu` VALUES ('1029', '登录删除', 202, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'monitor:logininfor:remove', '#', 1, sysdate(), NULL, NULL, '');

-- 超级管理员拥有所有菜单权限
INSERT INTO `sys_role_menu` SELECT 1, `menu_id` FROM `sys_menu`;

-- 字典类型
INSERT INTO `sys_dict_type` VALUES (1,  '用户性别', 'sys_user_sex',        '0', 1, sysdate(), NULL, NULL, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES (2,  '菜单状态', 'sys_show_hide',       '0', 1, sysdate(), NULL, NULL, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES (3,  '系统开关', 'sys_normal_disable',   '0', 1, sysdate(), NULL, NULL, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES (6,  '是否唯一', 'sys_yes_no',          '0', 1, sysdate(), NULL, NULL, '是否唯一列表');

-- 字典数据
INSERT INTO `sys_dict_data` VALUES (1,  1,  '男',   '0', 'sys_user_sex',   '',   'primary',   'Y', '0', 1, sysdate(), NULL, NULL, '性别男');
INSERT INTO `sys_dict_data` VALUES (2,  2,  '女',   '1', 'sys_user_sex',   '',   'danger',    'N', '0', 1, sysdate(), NULL, NULL, '性别女');
INSERT INTO `sys_dict_data` VALUES (3,  3,  '未知', '2', 'sys_user_sex',   '',   'info',      'N', '0', 1, sysdate(), NULL, NULL, '性别未知');
INSERT INTO `sys_dict_data` VALUES (4,  1,  '显示', '0', 'sys_show_hide',  '',   'primary',   'Y', '0', 1, sysdate(), NULL, NULL, '显示');
INSERT INTO `sys_dict_data` VALUES (5,  2,  '隐藏', '1', 'sys_show_hide',  '',   'danger',    'N', '0', 1, sysdate(), NULL, NULL, '隐藏');
INSERT INTO `sys_dict_data` VALUES (6,  1,  '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 1, sysdate(), NULL, NULL, '正常');
INSERT INTO `sys_dict_data` VALUES (7,  2,  '停用', '1', 'sys_normal_disable', '', 'danger',  'N', '0', 1, sysdate(), NULL, NULL, '停用');
INSERT INTO `sys_dict_data` VALUES (12, 1,  '是',   'Y', 'sys_yes_no',      '',   'primary',   'Y', '0', 1, sysdate(), NULL, NULL, '是');
INSERT INTO `sys_dict_data` VALUES (13, 2,  '否',   'N', 'sys_yes_no',      '',   'danger',    'N', '0', 1, sysdate(), NULL, NULL, '否');
