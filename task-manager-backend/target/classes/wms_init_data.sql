-- 商品管理目录（一级）
INSERT INTO sys_menu VALUES ('4', '商品管理', 0, 2, 'wms', NULL, '', '', 1, 0, 'M', '0', '0', '', 'shopping', 1, sysdate(), NULL, NULL, '商品管理目录');

-- 仓库管理菜单（二级）
INSERT INTO sys_menu VALUES ('401', '仓库管理', 4, 1, 'warehouse', 'wms/warehouse/index', '', '', 1, 0, 'C', '0', '0', 'wms:warehouse:list', 'warehouse', 1, sysdate(), NULL, NULL, '仓库管理菜单');

-- 商品管理菜单（二级）
INSERT INTO sys_menu VALUES ('402', '商品管理', 4, 2, 'product', 'wms/product/index', '', '', 1, 0, 'C', '0', '0', 'wms:product:list', 'component', 1, sysdate(), NULL, NULL, '商品管理菜单');

-- 仓库管理按钮
INSERT INTO sys_menu VALUES ('4001', '仓库查询', 401, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:warehouse:query', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4002', '仓库新增', 401, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:warehouse:add', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4003', '仓库修改', 401, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:warehouse:edit', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4004', '仓库删除', 401, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:warehouse:remove', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4005', '仓库导出', 401, 5, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:warehouse:export', '#', 1, sysdate(), NULL, NULL, '');

-- 商品管理按钮
INSERT INTO sys_menu VALUES ('4011', '商品查询', 402, 1, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:query', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4012', '商品新增', 402, 2, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:add', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4013', '商品修改', 402, 3, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:edit', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4014', '商品删除', 402, 4, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:remove', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4015', '商品导出', 402, 5, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:export', '#', 1, sysdate(), NULL, NULL, '');
INSERT INTO sys_menu VALUES ('4016', '商品导入', 402, 6, '', '', '', '', 0, 0, 'F', '0', '0', 'wms:product:import', '#', 1, sysdate(), NULL, NULL, '');

-- 超级管理员拥有商品管理菜单权限
INSERT INTO sys_role_menu VALUES ('1', '4');
INSERT INTO sys_role_menu VALUES ('1', '401');
INSERT INTO sys_role_menu VALUES ('1', '4001');
INSERT INTO sys_role_menu VALUES ('1', '4002');
INSERT INTO sys_role_menu VALUES ('1', '4003');
INSERT INTO sys_role_menu VALUES ('1', '4004');
INSERT INTO sys_role_menu VALUES ('1', '4005');
INSERT INTO sys_role_menu VALUES ('1', '402');
INSERT INTO sys_role_menu VALUES ('1', '4011');
INSERT INTO sys_role_menu VALUES ('1', '4012');
INSERT INTO sys_role_menu VALUES ('1', '4013');
INSERT INTO sys_role_menu VALUES ('1', '4014');
INSERT INTO sys_role_menu VALUES ('1', '4015');
INSERT INTO sys_role_menu VALUES ('1', '4016');

-- 仓库类型字典
INSERT INTO sys_dict_type VALUES (20, '仓库类型', 'wms_warehouse_type', '0', 1, sysdate(), NULL, NULL, '仓库类型列表');
INSERT INTO sys_dict_data VALUES (200, 1, '自有', '0', 'wms_warehouse_type', '', 'primary', 'Y', '0', 1, sysdate(), NULL, NULL, '自有仓库');
INSERT INTO sys_dict_data VALUES (201, 2, '租赁', '1', 'wms_warehouse_type', '', 'success', 'N', '0', 1, sysdate(), NULL, NULL, '租赁仓库');

-- 商品状态字典
INSERT INTO sys_dict_type VALUES (21, '商品状态', 'wms_product_status', '0', 1, sysdate(), NULL, NULL, '商品状态列表');
INSERT INTO sys_dict_data VALUES (210, 1, '上架', '0', 'wms_product_status', '', 'success', 'Y', '0', 1, sysdate(), NULL, NULL, '上架');
INSERT INTO sys_dict_data VALUES (211, 2, '下架', '1', 'wms_product_status', '', 'danger', 'N', '0', 1, sysdate(), NULL, NULL, '下架');

-- 是否默认供应商字典
INSERT INTO sys_dict_type VALUES (22, '是否默认', 'wms_is_default', '0', 1, sysdate(), NULL, NULL, '是否默认供应商');
INSERT INTO sys_dict_data VALUES (220, 1, '否', '0', 'wms_is_default', '', 'info', 'Y', '0', 1, sysdate(), NULL, NULL, '否');
INSERT INTO sys_dict_data VALUES (221, 2, '是', '1', 'wms_is_default', '', 'primary', 'N', '0', 1, sysdate(), NULL, NULL, '是');
