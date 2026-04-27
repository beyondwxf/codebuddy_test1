-- ============================================================
-- 全场景测试数据生成脚本
-- 覆盖：用户管理、角色权限、部门组织、供应商管理、商品WMS、日志审计等全部业务场景
-- ============================================================

USE `ruoyi_admin`;

-- ============================================================
-- 一、部门扩展数据（多层级 + 停用 + 子部门）
-- 场景：多级部门树、停用部门、跨区域分布
-- ============================================================
INSERT INTO `sys_dept` VALUES (106, 102, '0,100,102', '研发一部',   1, '张三', '13800001001', 'dev1@ry.com',     '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (107, 102, '0,100,102', '研发二部',   2, '李四', '13800001002', 'dev2@ry.com',     '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (108, 103, '0,100,101,103', '前端组', 1, '王五', '13800001003', 'front@ry.com',    '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (109, 103, '0,100,101,103', '后端组', 2, '赵六', '13800001004', 'backend@ry.com',   '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (110, 104, '0,100,101,104', '销售一组', 1, '钱七', '13800001005', 'sale1@ry.com',    '0', '0', 1, sysdate(), NULL, NULL);
INSERT INTO `sys_dept` VALUES (111, 104, '0,100,101,104', '销售二组', 2, '孙八', '13800001006', 'sale2@ry.com',    '0', '0', 1, sysdate(), NULL, NULL);
-- 停用部门（用于测试停用部门的过滤逻辑）
INSERT INTO `sys_dept` VALUES (112, 101, '0,100,101', '待解散部门', 9, '周九', '13800001007', 'closed@ry.com',   '1', '0', 1, sysdate(), NULL, NULL);

-- ============================================================
-- 二、角色扩展数据
-- 场景：自定义数据范围、停用角色
-- ============================================================
-- 普通管理员角色（本部门数据范围）
INSERT INTO `sys_role` VALUES (3, '部门管理员', 'dept_admin', 3, '3', 1, 1, '0', '0', 1, sysdate(), NULL, NULL, '仅管理部门内数据');
-- 只读角色（仅本人数据范围）
INSERT INTO `sys_role` VALUES (4, '只读用户',   'readonly',   4, '5', 1, 1, '0', '0', 1, sysdate(), NULL, NULL, '只能查看自己创建的数据');
-- 业务员角色（本部门及以下数据范围）
INSERT INTO `sys_role` VALUES (5, '业务员',     'sales',      5, '4', 1, 1, '0', '0', 1, sysdate(), NULL, NULL, '销售人员');
-- 停用角色（用于测试停用角色的过滤逻辑）
INSERT INTO `sys_role` VALUES (6, '已停用角色', 'disabled',   9, '1', 1, 1, '1', '0', 1, sysdate(), NULL, NULL, '此角色已被停用');

-- 部门管理员分配系统管理和商品管理的查看权限
INSERT INTO `sys_role_menu` VALUES (3, 101);
INSERT INTO `sys_role_menu` VALUES (3, 102);
INSERT INTO `sys_role_menu` VALUES (3, 401);
INSERT INTO `sys_role_menu` VALUES (3, 402);
INSERT INTO `sys_role_menu` VALUES (3, 301);
INSERT INTO `sys_role_menu` VALUES (3, 1001);
INSERT INTO `sys_role_menu` VALUES (3, 1007);
INSERT INTO `sys_role_menu` VALUES (3, 4001);
INSERT INTO `sys_role_menu` VALUES (3, 4011);
INSERT INTO `sys_role_menu` VALUES (3, 3001);

-- 只读用户分配查询权限
INSERT INTO `sys_role_menu` VALUES (4, 101);
INSERT INTO `sys_role_menu` VALUES (4, 102);
INSERT INTO `sys_role_menu` VALUES (4, 301);
INSERT INTO `sys_role_menu` VALUES (4, 401);
INSERT INTO `sys_role_menu` VALUES (4, 402);
INSERT INTO `sys_role_menu` VALUES (4, 1001);
INSERT INTO `sys_role_menu` VALUES (4, 1007);
INSERT INTO `sys_role_menu` VALUES (4, 3001);
INSERT INTO `sys_role_menu` VALUES (4, 4001);
INSERT INTO `sys_role_menu` VALUES (4, 4011);

-- 业务员分配供应商和商品的增删改查权限
INSERT INTO `sys_role_menu` VALUES (5, 301);
INSERT INTO `sys_role_menu` VALUES (5, 3001);
INSERT INTO `sys_role_menu` VALUES (5, 3002);
INSERT INTO `sys_role_menu` VALUES (5, 3003);
INSERT INTO `sys_role_menu` VALUES (5, 3004);
INSERT INTO `sys_role_menu` VALUES (5, 402);
INSERT INTO `sys_role_menu` VALUES (5, 4011);
INSERT INTO `sys_role_menu` VALUES (5, 4012);
INSERT INTO `sys_role_menu` VALUES (5, 4013);
INSERT INTO `sys_role_menu` VALUES (5, 4014);

-- ============================================================
-- 三、用户扩展数据（核心测试数据）
-- 场景：正常/停用/已删除用户、各性别、各部门、各角色
-- 密码统一使用 BCrypt 加密的 "Admin@2026"
-- ============================================================

-- 正常用户 - 研发部门（深圳）
-- user_id=3: 研发经理，男，部门管理员角色
INSERT INTO `sys_user` VALUES (3, 103, 'zhangsan', '张三', '00', 'zhangsan@ry.com', '13900001001', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.10', '2026-04-20 09:00:00', 1, sysdate(), NULL, NULL, '研发部门经理');

-- user_id=4: 后端开发，男，普通角色
INSERT INTO `sys_user` VALUES (4, 109, 'lisi', '李四', '00', 'lisi@ry.com', '13900001002', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.11', '2026-04-21 08:30:00', 1, sysdate(), NULL, NULL, '后端开发工程师');

-- user_id=5: 前端开发，女，普通角色
INSERT INTO `sys_user` VALUES (5, 108, 'wangwu', '王五', '00', 'wangwu@ry.com', '13900001003', '1', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.12', '2026-04-22 10:15:00', 1, sysdate(), NULL, NULL, '前端开发工程师');

-- user_id=6: 测试工程师，未知性别，普通角色
INSERT INTO `sys_user` VALUES (6, 105, 'zhaoliu', '赵六', '00', 'zhaoliu@ry.com', '13900001004', '2', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.13', '2026-04-23 14:20:00', 1, sysdate(), NULL, NULL, '测试工程师');

-- 正常用户 - 市场部门（深圳）
-- user_id=7: 销售主管，男，业务员角色
INSERT INTO `sys_user` VALUES (7, 104, 'qianqi', '钱七', '00', 'qianqi@ry.com', '13900001005', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.14', '2026-04-24 09:45:00', 1, sysdate(), NULL, NULL, '销售主管');

-- user_id=8: 销售代表，女，业务员角色
INSERT INTO `sys_user` VALUES (8, 110, 'sunba', '孙八', '00', 'sunba@ry.com', '13900001006', '1', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.1.15', '2026-04-25 11:30:00', 1, sysdate(), NULL, NULL, '销售代表');

-- 正常用户 - 长沙分公司
-- user_id=9: 分公司研发，男，只读角色
INSERT INTO `sys_user` VALUES (9, 106, 'zhoujiu', '周九', '00', 'zhoujiu@ry.com', '13900001007', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.2.10', '2026-04-26 08:00:00', 1, sysdate(), NULL, NULL, '长沙研发一部');

-- user_id=10: 分公司测试，女，只读角色
INSERT INTO `sys_user` VALUES (10, 107, 'wushi', '吴十', '00', 'wushi@ry.com', '13900001008', '1', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '192.168.2.11', '2026-04-27 13:00:00', 1, sysdate(), NULL, NULL, '长沙研发二部');

-- 停用用户（status='1'，用于测试停用账号登录拦截）
INSERT INTO `sys_user` VALUES (11, 103, 'disabled_user', '停用账号', '00', 'disabled@ry.com', '13900001099', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '1', '0', '192.168.1.99', '2026-03-01 10:00:00', 1, sysdate(), NULL, NULL, '此账号已被停用');

-- 已删除用户（del_flag='2'，用于测试逻辑删除查询过滤）
INSERT INTO `sys_user` VALUES (12, 105, 'deleted_user', '已删除账号', '00', 'deleted@ry.com', '13900001088', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '2', '192.168.1.88', '2026-02-01 10:00:00', 1, sysdate(), NULL, NULL, '此账号已被删除');

-- 无部门用户（dept_id=NULL，用于测试空值边界）
INSERT INTO `sys_user` VALUES (13, NULL, 'nodept_user', '无部门用户', '00', 'nodept@ry.com', '13900001077', '2', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', NULL, NULL, 1, sysdate(), NULL, NULL, '未分配部门的用户');

-- 用户角色关联
INSERT INTO `sys_user_role` VALUES (3, 3);  -- 张三 -> 部门管理员
INSERT INTO `sys_user_role` VALUES (4, 2);  -- 李四 -> 普通角色
INSERT INTO `sys_user_role` VALUES (5, 2);  -- 王五 -> 普通角色
INSERT INTO `sys_user_role` VALUES (6, 2);  -- 赵六 -> 普通角色
INSERT INTO `sys_user_role` VALUES (7, 5);  -- 钱七 -> 业务员
INSERT INTO `sys_user_role` VALUES (8, 5);  -- 孙八 -> 业务员
INSERT INTO `sys_user_role` VALUES (9, 4);  -- 周九 -> 只读用户
INSERT INTO `sys_user_role` VALUES (10, 4); -- 吴十 -> 只读用户
INSERT INTO `sys_user_role` VALUES (11, 2); -- 停用账号 -> 普通角色

-- ============================================================
-- 四、供应商扩展数据
-- 场景：5种联系状态全覆盖、多省份、多品类、已删除供应商
-- ============================================================
-- 联系状态 0: 未联系
INSERT INTO `sys_supplier` VALUES (1,  '深圳市华强电子有限公司',       '广东', '张总', '0755-88880001', '13900002001', NULL, NULL, '0', '电子产品',           '广东省深圳市福田区华强北路1号电子大厦A座1201',                          '0', 1, sysdate(), NULL, NULL, '主营电子元器件批发');
INSERT INTO `sys_supplier` VALUES (2,  '北京中科机械设备制造厂',       '北京', '李工', '010-88880002',  '13900002002', NULL, NULL, '0', '机械设备',           '北京市海淀区中关村科技园区创新路88号',                                   '0', 1, sysdate(), NULL, NULL, '精密机械加工设备');
INSERT INTO `sys_supplier` VALUES (3,  '四川成都建材贸易公司',         '四川', '王经理', '028-88880003', '13900002003', NULL, NULL, '0', '建材',               '四川省成都市武侯区天府大道中段666号',                                  '0', 1, sysdate(), NULL, NULL, '钢材水泥批发');

-- 联系状态 1: 已加微信
INSERT INTO `sys_supplier` VALUES (4,  '上海优品食品有限公司',         '上海', '陈小姐', '021-88880004', '13900002004', NULL, NULL, '1', '食品饮料',           '上海市浦东新区张江高科园区科苑路188号',                                 '0', 1, sysdate(), NULL, NULL, '微信已添加，每周有促销活动');
INSERT INTO `sys_supplier` VALUES (5,  '广州南方纺织服装集团',         '广东', '刘厂长', '020-88880005', '13900002005', NULL, NULL, '1', '纺织服装',           '广东省广州市海珠区新港中路398号',                                     '0', 1, sysdate(), NULL, NULL, '微信：gz_textile_88，主要做面料供应');
INSERT INTO `sys_supplier` VALUES (6,  '浙江杭州化工原料进出口公司',   '浙江', '赵博士', '0571-88880006', '13900002006', NULL, NULL, '1', '化工原料',           '浙江省杭州市滨江区网商路599号',                                       '0', 1, sysdate(), NULL, NULL, '化工原料进口渠道稳定');

-- 联系状态 2: 未接
INSERT INTO `sys_supplier` VALUES (7,  '江苏南京办公用品配送中心',     '江苏', '周老板', '025-88880007', '13900002007', '15900002007', NULL, '2', '办公用品',           '江苏省南京市建邺区江东中路211号',                                      '0', 1, sysdate(), NULL, NULL, '多次拨打无人接听，需上门拜访');
INSERT INTO `sys_supplier` VALUES (8,  '湖北武汉物流仓储设备公司',     '湖北', '吴经理', '027-88880008', '13900002008', '15900002008', NULL, '2', '机械设备',           '湖北省武汉市东西湖区金银湖大道88号',                                    '0', 1, sysdate(), NULL, NULL, '电话多次未接，建议换联系方式');

-- 联系状态 3: 空号
INSERT INTO `sys_supplier` VALUES (9,  '福建厦门电子配件经销商',       '福建', '郑先生', '0592-88880099', '13900009999', NULL, NULL, '3', '电子产品',           '福建省厦门市思明区湖滨南路90号',                                        '0', 1, sysdate(), NULL, NULL, '电话号码已成空号，需更新联系方式');
INSERT INTO `sys_supplier` VALUES (10, '山东青岛海洋食品加工厂',       '山东', '孙厂长', '0532-88880099', '13900009998', NULL, NULL, '3', '食品饮料',           '山东省青岛市市南区香港中路68号',                                         '0', 1, sysdate(), NULL, NULL, '原联系电话已停机');

-- 联系状态 4: 已下单
INSERT INTO `sys_supplier` VALUES (11, '河南郑州建材批发市场旗舰店',   '河南', '马总',   '0371-88880010', '13900002010', NULL, NULL, '4', '建材',               '河南省郑州市管城回族区郑汴路88号建材大世界A区',                     '0', 1, sysdate(), NULL, NULL, '长期合作供应商，月均下单5次以上');
INSERT INTO `sys_supplier` VALUES (12, '湖南长沙办公耗材专营店',       '湖南', '黄店长', '0731-88880011', '13900002011', NULL, NULL, '4', '办公用品',           '湖南省长沙市岳麓区枫林二路188号',                                      '0', 1, sysdate(), NULL, NULL, '首批采购已完成交付，质量合格');

-- 多品类供应商（category包含多个品类，逗号分隔）
INSERT INTO `sys_supplier` VALUES (13, '天津华北综合贸易公司',         '天津', '杨总',   '022-88880012', '13900002012', NULL, NULL, '1', '电子产品,机械设备,化工原料', '天津市滨海新区开发区第五大街99号',                                     '0', 1, sysdate(), NULL, NULL, '综合型贸易公司，品类齐全');

-- 已删除供应商（del_flag='2'）
INSERT INTO `sys_supplier` VALUES (14, '陕西西安废弃供应商',           '陕西', '贾老板', '029-88880013', '13900002013', NULL, NULL, '0', '纺织服装',           '陕西省西安市雁塔区科技路10号',                                           '2', 1, sysdate(), NULL, NULL, '此供应商已停止合作并标记删除');

-- ============================================================
-- 五、仓库扩展数据
-- 场景：自有/租赁仓库、正常/停用、多省份分布
-- ============================================================
-- 自有仓库 - 正常
INSERT INTO `wms_warehouse` VALUES (1,  '深圳总仓',      'SZ_WH_001', '广东', '广东省深圳市宝安区福永街道宝安大道6008号',                        '0', '0', '0', 1, sysdate(), NULL, NULL, '主仓库，面积5000平');
INSERT INTO `wms_warehouse` VALUES (2,  '长沙一号仓',    'CS_WH_001', '湖南', '湖南省长沙市长沙县黄花镇机场大道88号物流园',                    '0', '0', '0', 1, sysdate(), NULL, NULL, '华中地区中心仓');
INSERT INTO `wms_warehouse` VALUES (3,  '北京朝阳仓',    'BJ_WH_001', '北京', '北京市朝阳区十八里店乡吕家营村物流基地C区',                      '0', '0', '0', 1, sysdate(), NULL, NULL, '华北地区中心仓');
INSERT INTO `wms_warehouse` VALUES (4,  '上海嘉定仓',    'SH_WH_001', '上海', '上海市嘉定区外冈镇工业园区恒飞路508号',                         '0', '0', '0', 1, sysdate(), NULL, NULL, '华东地区中心仓');
-- 租赁仓库 - 正常
INSERT INTO `wms_warehouse` VALUES (5,  '广州临时仓',    'GZ_TMP_001','广东', '广东省广州市白云区太和镇林安物流园B12栋',                          '1', '0', '0', 1, sysdate(), NULL, NULL, '旺季临时租赁仓库，租期至2026年12月');
INSERT INTO `wms_warehouse` VALUES (6,  '成都代发仓',    'CD_DF_001', '四川', '四川省成都市双流区西航港大道中段999号电商产业园',                  '1', '0', '0', 1, sysdate(), NULL, NULL, '西南电商代发专用仓');
-- 自有仓库 - 停用
INSERT INTO `wms_warehouse` VALUES (7,  '武汉旧仓',      'WH_OLD_001','湖北', '湖北省武汉市洪山区雄楚大道288号',                                   '0', '1', '0', 1, sysdate(), NULL, NULL, '老旧仓库已停用等待搬迁');
-- 租赁仓库 - 已删除
INSERT INTO `wms_warehouse` VALUES (8,  '西安已退租仓',  'XA_END_001','陕西', '陕西省西安市未央区三桥街道天台路88号',                               '1', '0', '2', 1, sysdate(), NULL, NULL, '合同到期已退租');

-- ============================================================
-- 六、商品扩展数据
-- 场景：上架/下架、不同价格区间、多单位、已删除
-- ============================================================
-- 上架商品 - 电子产品类
INSERT INTO `wms_product` VALUES (1,  '无线蓝牙耳机 Pro Max',          'BT-HEADSET-PROMAX',   '高品质主动降噪蓝牙耳机，续航40小时，支持快充',              '/images/product/headset.jpg', '<p>产品亮点：</p><ul><li>主动降噪技术</li><li>40小时超长续航</li><li>蓝牙5.3协议</li></ul>', 599.00,   280.00,  '个',  '0', '0', 1, sysdate(), NULL, NULL, '热销爆款');
INSERT INTO `wms_product` VALUES (2,  '智能手表 S8 运动版',            'WATCH-S8-SPORT',       '心率监测+GPS定位+50米防水，运动健康首选',                      '/images/product/watch.jpg',  '<p>专业运动监测</p><p>支持100+种运动模式</p>',                                1299.00,  650.00,  '块',  '0', '0', 1, sysdate(), NULL, NULL, '新品上市');
INSERT INTO `wms_product` VALUES (3,  'Type-C 快充数据线 1.5米',       'USB-C-CABLE-1.5M',    'PD 60W快充协议，编织线身耐用抗拉',                              '/images/product/cable.jpg',  '<p>兼容主流设备</p>',                                                      29.90,    5.50,  '根',  '0', '0', 1, sysdate(), NULL, NULL, '引流款');
INSERT INTO `wms_product` VALUES (4,  '便携式移动电源 20000mAh',      'POWER-BANK-20000',    '双向快充，LED电量显示，可上飞机',                               '/images/product/powerbank.jpg', '<p>20000mAh大容量</p><p>支持18W双向快充</p>',                             149.00,   65.00,  '个',  '0', '0', 1, sysdate(), NULL, NULL, '');

-- 上架商品 - 办公用品类
INSERT INTO `wms_product` VALUES (5,  'A4复印纸 70g (500张/包)',        'PAPER-A4-70G',        '高白度复印纸，适用于激光打印机和喷墨打印机',                   '/images/product/paper.jpg',  '<p>规格：210×297mm</p><p>白度≥95%</p>',                                   25.80,   15.00,  '包',  '0', '0', 1, sysdate(), NULL, NULL, '日常消耗品');
INSERT INTO `wms_product` VALUES (6,  '中性笔芯 (0.5mm 黑色 20支装)',  'PEN-REFILL-05BK-20',  '顺滑书写不卡墨，适用大多数中性笔壳',                             '/images/product/refill.jpg', '<p>0.5mm子弹头</p><p>黑色墨水</p>',                                         9.90,     2.80,  '袋',  '0', '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product` VALUES (7,  '多功能文件收纳盒 A4横式',       'FILE-BOX-A4-H',        '加厚PP材质，可叠放节省空间',                                     '/images/product/filebox.jpg', '<p>A4纸尺寸适配</p><p>可堆叠设计</p>',                                     35.00,   12.00,  '个',  '0', '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product` VALUES (8,  '无线静音鼠标 (充电版)',         'MOUSE-WLESS-CHARGE',  '人体工学设计，1600DPI可调，Type-C充电',                         '/images/product/mouse.jpg',  '<p>静音点击</p><p>续航30天</p>',                                          79.00,    32.00,  '个',  '0', '0', 1, sysdate(), NULL, NULL, '');

-- 上架商品 - 其他类别
INSERT INTO `wms_product` VALUES (9,  '工业级防护手套 (L码 12双装)',  'SAFETY-GLOVES-L-12',  '耐磨防割乳胶涂层手套，适合仓储搬运作业',                       '/images/product/gloves.jpg',  '<p>乳胶涂层掌心</p><p>针织袖口</p>',                                        58.00,   22.00,  '包',  '0', '0', 1, sysdate(), NULL, NULL, '劳保用品');
INSERT INTO `wms_product` VALUES (10, '透明胶带封箱带 (宽4.5cm*100y)', 'TAPE-CLEAR-4.5-100Y', '高粘度透明封箱胶带，不易断带',                                 '/images/product/tape.jpg',   '<p>厚度2.0cm</p><p>长度100码</p>',                                         3.50,    1.20,  '卷',  '0', '0', 1, sysdate(), NULL, NULL, '包装耗材');
INSERT INTO `wms_product` VALUES (11, '企业定制LOGO文化衫(白色 M)',   'TSHIRT-CUSTOM-W-M',   '纯棉圆领T恤，支持丝印/刺绣LOGO定制，MOQ 50件起',             '/images/product/tshirt.jpg',  '<p>180g纯棉面料</p><p>支持定制</p>',                                        45.00,   18.00,  '件',  '0', '0', 1, sysdate(), NULL, NULL, '需提前预订');
INSERT INTO `wms_product` VALUES (12, '矿泉水 555ml * 24瓶/箱',        'WATER-555ML-24',      '纯天然矿泉水，符合国家饮用水标准',                               '/images/product/water.jpg',   '<p>555ml*24瓶整箱</p><p>塑膜包装</p>',                                     28.00,   16.00,  '箱',  '0', '0', 1, sysdate(), NULL, NULL, '');

-- 下架商品（status='1'，用于测试下架商品不在前台展示）
INSERT INTO `wms_product` VALUES (13, '旧款有线键盘 USB接口',         'KB-OLD-USB',          '标准104键有线薄膜键盘',                                             '/images/product/kb_old.jpg',  '<p>经典款式</p>',                                                          49.90,   25.00,  '个',  '1', '0', 1, sysdate(), NULL, NULL, '产品已迭代，旧款下架');
INSERT INTO `wms_product` VALUES (14, '过季冬季保暖围巾',             'SCARF-WINTER-OLD',    '加厚针织围巾，多色可选',                                           '/images/product/scarf.jpg',  '<p>冬季保暖</p>',                                                            69.00,    30.00,  '条',  '1', '0', 1, sysdate(), NULL, NULL, '季节性商品已下架');

-- 已删除商品（del_flag='2'）
INSERT INTO `wms_product` VALUES (15, '测试商品-待清理',               'TEST-DEL-001',         '这是一个用于测试逻辑删除的商品记录',                               NULL,                 NULL,                                                                     1.00,     0.50,  '件',  '0', '2', 1, sysdate(), NULL, NULL, '已标记删除');

-- ============================================================
-- 七、商品供应商关联数据
-- 场景：一商品多供应商、默认供应商、非默认供应商
-- ============================================================
-- 蓝牙耳机的多个供应商
INSERT INTO `wms_product_supplier` VALUES (1,  1,  1,  'SUP-BT-001', 320.00, 7,  '1', '0', 1, sysdate(), NULL, NULL, '默认供应商，质量稳定交期准');
INSERT INTO `wms_product_supplier` VALUES (2,  1,  4,  'SUP-BT-002', 295.00, 10, '0', '0', 1, sysdate(), NULL, NULL, '备选供应商，价格略低但交期较长');
INSERT INTO `wms_product_supplier` VALUES (3,  1,  13, 'SUP-BT-003', 290.00, 14, '0', '0', 1, sysdate(), NULL, NULL, '综合贸易商，可与其他产品拼单');

-- 智能手表的供应商
INSERT INTO `wms_product_supplier` VALUES (4,  2,  1,  'SUP-WATCH-001', 680.00, 15, '1', '0', 1, sysdate(), NULL, NULL, '默认供应商');
INSERT INTO `wms_product_supplier` VALUES (5,  2,  13, 'SUP-WATCH-002', 660.00, 20, '0', '0', 1, sysdate(), NULL, NULL, '备选供应商');

-- 数据线的供应商
INSERT INTO `wms_product_supplier` VALUES (6,  3,  1,  'SUP-CABLE-001', 6.00,   5,  '1', '0', 1, sysdate(), NULL, NULL, '默认供应商，量大从优');

-- 复印纸的供应商
INSERT INTO `wms_product_supplier` VALUES (7,  5,  11, 'SUP-PAPER-001', 14.00, 3,  '1', '0', 1, sysdate(), NULL, NULL, '本地建材供应商也提供纸张');
INSERT INTO `wms_product_supplier` VALUES (8,  5,  12, 'SUP-PAPER-002', 15.50, 2,  '0', '0', 1, sysdate(), NULL, NULL, '长沙本地供应商');

-- 中性笔芯的供应商
INSERT INTO `wms_product_supplier` VALUES (9,  6,  12, 'SUP-PEN-001',   3.00,   3,  '1', '0', 1, sysdate(), NULL, NULL, '默认供应商');
INSERT INTO `wms_product_supplier` VALUES (10, 6,  11, 'SUP-PEN-002',   3.20,   5,  '0', '0', 1, sysdate(), NULL, NULL, '备选');

-- 防护手套的供应商
INSERT INTO `wms_product_supplier` VALUES (11, 9,  11, 'SUP-GLOVE-001', 23.00, 3,  '1', '0', 1, sysdate(), NULL, NULL, '默认供应商');
INSERT INTO `wms_product_supplier` VALUES (12, 9,  2,  'SUP-GLOVE-002', 26.00, 10, '0', '0', 1, sysdate(), NULL, NULL, '北方备选供应商');

-- 矿泉水的供应商
INSERT INTO `wms_product_supplier` VALUES (13, 12, 5,  'SUP-WATER-001', 17.00, 1,  '1', '0', 1, sysdate(), NULL, NULL, '广州本地水饮供应商');
INSERT INTO `wms_product_supplier` VALUES (14, 12, 11, 'SUP-WATER-002', 17.50, 2,  '0', '0', 1, sysdate(), NULL, NULL, '河南本地备选');

-- ============================================================
-- 八、商品库存数据
-- 场景：充足库存、低库存（接近预警）、零库存（低于预警）、负库存（超额出库）、多仓分布
-- ============================================================
-- 蓝牙耳机库存分布
INSERT INTO `wms_product_inventory` VALUES (1,  1,  1, 2500,  200,  '0', 1, sysdate(), NULL, NULL, '深圳主仓库存充足');
INSERT INTO `wms_product_inventory` VALUES (2,  1,  2, 800,   100,  '0', 1, sysdate(), NULL, NULL, '长沙分仓');
INSERT INTO `wms_product_inventory` VALUES (3,  1,  3, 1500,  200,  '0', 1, sysdate(), NULL, NULL, '北京仓');
INSERT INTO `wms_product_inventory` VALUES (4,  1,  4, 1200,  150,  '0', 1, sysdate(), NULL, NULL, '上海仓');

-- 智能手表库存
INSERT INTO `wms_product_inventory` VALUES (5,  2,  1, 350,   50,   '0', 1, sysdate(), NULL, NULL, '新品库存');
INSERT INTO `wms_product_inventory` VALUES (6,  2,  2, 120,   30,   '0', 1, sysdate(), NULL, NULL, '');

-- 数据线库存 - 高周转
INSERT INTO `wms_product_inventory` VALUES (7,  3,  1, 15000, 2000, '0', 1, sysdate(), NULL, NULL, '引流款备货充足');
INSERT INTO `wms_product_inventory` VALUES (8,  3,  5, 5000,  1000, '0', 1, sysdate(), NULL, NULL, '临时仓存放');

-- 移动电源库存 - 低库存预警（接近预警值）
INSERT INTO `wms_product_inventory` VALUES (9,  4,  1, 255,   250,  '0', 1, sysdate(), NULL, NULL, '接近预警线，需补货');
INSERT INTO `wms_product_inventory` VALUES (10, 4,  2, 60,    50,   '0', 1, sysdate(), NULL, NULL, '');

-- 复印纸库存 - 零库存（低于预警）
INSERT INTO `wms_product_inventory` VALUES (11, 5,  1, 8,     50,   '0', 1, sysdate(), NULL, NULL, '严重缺货！急需补货');
INSERT INTO `wms_product_inventory` VALUES (12, 5,  3, 15,    30,   '0', 1, sysdate(), NULL, NULL, '库存不足');

-- 中性笔芯库存
INSERT INTO `wms_product_inventory` VALUES (13, 6,  1, 5200,  500,  '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product_inventory` VALUES (14, 6,  2, 1800,  200,  '0', 1, sysdate(), NULL, NULL, '');

-- 收纳盒库存
INSERT INTO `wms_product_inventory` VALUES (15, 7,  1, 420,   100,  '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product_inventory` VALUES (16, 7,  4, 280,   80,   '0', 1, sysdate(), NULL, NULL, '');

-- 鼠标库存
INSERT INTO `wms_product_inventory` VALUES (17, 8,  1, 680,   150,  '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product_inventory` VALUES (18, 8,  4, 320,   80,   '0', 1, sysdate(), NULL, NULL, '');

-- 防护手套库存
INSERT INTO `wms_product_inventory` VALUES (19, 9,  1, 1500,  200,  '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product_inventory` VALUES (20, 9,  2, 450,   100,  '0', 1, sysdate(), NULL, NULL, '');

-- 封箱胶带库存
INSERT INTO `wms_product_inventory` VALUES (21, 10, 1,  85000, 5000,'0', 1, sysdate(), NULL, NULL, '包装耗材大量储备');

-- 文化衫库存
INSERT INTO `wms_product_inventory` VALUES (22, 11, 1,  200,   50,   '0', 1, sysdate(), NULL, NULL, '定制商品按订单生产');

-- 矿泉水库存
INSERT INTO `wms_product_inventory` VALUES (23, 12, 1,  380,   100,  '0', 1, sysdate(), NULL, NULL, '');
INSERT INTO `wms_product_inventory` VALUES (24, 12, 5,  200,   50,   '0', 1, sysdate(), NULL, NULL, '广州临时仓存放');

-- 下架商品的残留库存（验证下架商品库存是否仍可见）
INSERT INTO `wms_product_inventory` VALUES (25, 13, 1,  85,    30,   '0', 1, sysdate(), NULL, NULL, '下架商品剩余库存');

-- ============================================================
-- 九、操作日志测试数据
-- 场景：正常操作、异常操作、各模块覆盖、不同业务类型
-- ============================================================
-- 用户管理操作
INSERT INTO `sys_oper_log` VALUES (1,  '用户管理', 1,  'com.taskmanager.controller.SysUserController.add()',       'POST',   1, 'admin',  '若依科技', '/api/system/user',       '127.0.0.1', '本地', '{"userName":"zhangsu","nickName":"张三","deptId":103}', '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 09:00:00', 156);
INSERT INTO `sys_oper_log` VALUES (2,  '用户管理', 2,  'com.taskmanager.controller.SysUserController.edit()',      'PUT',    1, 'admin',  '若依科技', '/api/system/user',       '127.0.0.1', '本地', '{"userId":3,"nickName":"张三（更新）"}',                           '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 09:30:00', 89);
INSERT INTO `sys_oper_log` VALUES (3,  '用户管理', 3,  'com.taskmanager.controller.SysUserController.remove()',    'DELETE', 1, 'admin',  '若依科技', '/api/system/user/12',    '127.0.0.1', '本地', '12',                                                        '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 10:00:00', 45);
INSERT INTO `sys_oper_log` VALUES (4,  '用户管理', 5,  'com.taskcontroller.SysUserController.resetPwd()',         'PUT',    1, 'admin',  '若依科技', '/api/system/user/resetPwd', '127.0.0.1', '本地', '{"userId":4}',                                              '{"code":200,"msg":"密码重置成功"}', 0, '', '2026-04-27 10:30:00', 67);
-- 异常操作
INSERT INTO `sys_oper_log` VALUES (5,  '用户管理', 1,  'com.taskcontroller.SysUserController.add()',           'POST',   1, 'admin',  '若依科技', '/api/system/user',       '127.0.0.1', '本地', '{"userName":""}',                                            '{"code":500,"msg":"用户名不能为空"}', 1, '用户名不能为空: 入参校验失败', '2026-04-27 11:00:00', 23);

-- 角色管理操作
INSERT INTO `sys_oper_log` VALUES (6,  '角色管理', 1,  'com.taskcontroller.SysRoleController.add()',           'POST',   1, 'admin',  '若依科技', '/api/system/role',       '127.0.0.1', '本地', '{"roleName":"测试角色","roleKey":"test"}',                   '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-26 14:00:00', 120);
INSERT INTO `sys_oper_log` VALUES (7,  '角色管理', 2,  'com.taskcontroller.SysRoleController.edit()',          'PUT',    1, 'admin',  '若依科技', '/api/system/role',       '127.0.0.1', '本地', '{"roleId":3,"roleName":"部门管理员（改）"}',                     '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-26 15:00:00', 95);

-- 供应商管理操作
INSERT INTO `sys_oper_log` VALUES (8,  '供应商管理', 1, 'com.taskcontroller.SupplierController.add()',         'POST',   1, 'qianqi', '市场部门', '/api/supplier',            '192.168.1.14', '深圳', '{"companyName":"测试供应商","province":"广东"}',                '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 08:00:00', 200);
INSERT INTO `sys_oper_log` VALUES (9,  '供应商管理', 2, 'com.taskcontroller.SupplierController.edit()',        'PUT',    1, 'qianqi', '市场部门', '/api/supplier',            '192.168.1.14', '深圳', '{"supplierId":1,"contactStatus":"4"}',                            '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 08:30:00', 150);
INSERT INTO `sys_oper_log` VALUES (10, '供应商管理', 3, 'com.taskcontroller.SupplierController.remove()',     'DELETE', 1, 'admin',  '若依科技', '/api/supplier/14',        '127.0.0.1', '本地', '14',                                                        '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-27 09:00:00', 55);

-- 仓库管理操作
INSERT INTO `sys_oper_log` VALUES (11, '仓库管理', 1, 'com.taskcontroller.WarehouseController.add()',          'POST',   1, 'admin',  '若依科技', '/api/wms/warehouse',       '127.0.0.1', '本地', '{"warehouseName":"测试仓","warehouseCode":"TEST"}',             '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-25 10:00:00', 180);
INSERT INTO `sys_oper_log` VALUES (12, '仓库管理', 2, 'com.taskcontroller.WarehouseController.edit()',         'PUT',    1, 'admin',  '若依科技', '/api/wms/warehouse',       '127.0.0.1', '本地', '{"warehouseId":1,"remark":"备注更新"}',                          '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-25 11:00:00', 92);

-- 商品管理操作
INSERT INTO `sys_oper_log` VALUES (13, '商品管理', 1, 'com.taskcontroller.ProductController.add()',           'POST',   1, 'admin',  '若依科技', '/api/wms/product',        '127.0.0.1', '本地', '{"productName":"测试商品","skuCode":"TEST-SKU"}',              '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-24 09:00:00', 230);
INSERT INTO `sys_oper_log` VALUES (14, '商品管理', 2, 'com.taskcontroller.ProductController.edit()',          'PUT',    1, 'admin',  '若依科技', '/api/wms/product',        '127.0.0.1', '本地', '{"productId":1,"salePrice":569.00}',                             '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-24 10:00:00', 110);
INSERT INTO `sys_oper_log` VALUES (15, '商品管理', 3, 'com.taskcontroller.ProductController.remove()',        'DELETE', 1, 'admin',  '若依科技', '/api/wms/product/15',     '127.0.0.1', '本地', '15',                                                        '{"code":200,"msg":"操作成功"}', 0, '',  '2026-04-24 11:00:00', 48);

-- 导入导出操作
INSERT INTO `sys_oper_log` VALUES (16, '商品管理', 0, 'com.taskcontroller.ProductController.importTemplate()','GET',    1, 'admin',  '若依科技', '/api/wms/product/importTemplate', '127.0.0.1', '本地', '{}',                                                         '{\"file\":\"template.xlsx\"}',       0, '',  '2026-04-23 16:00:00', 35);
INSERT INTO `sys_oper_log` VALUES (17, '供应商管理', 0, 'com.taskcontroller.SupplierController.export()',     'POST',   1, 'qianqi', '市场部门', '/api/supplier/export',     '192.168.1.14', '深圳', '{"supplierIds":"1,2,3,4"}',                                    '{\"file\":\"supplier.xlsx\"}',       0, '',  '2026-04-23 17:00:00', 1200);

-- ============================================================
-- 十、登录日志测试数据
-- 场景：成功登录、失败登录（密码错误/验证码错误/账号停用/账号不存在）、不同浏览器/OS/IP
-- ============================================================
-- 成功登录
INSERT INTO `sys_logininfor` VALUES (1,  'admin',    '127.0.0.1',  '本地',      'Chrome 125',    'Windows 11',    '0', '登录成功', '2026-04-27 08:59:00');
INSERT INTO `sys_logininfor` VALUES (2,  'admin',    '192.168.1.10','深圳办公室', 'Chrome 124',    'Windows 10',    '0', '登录成功', '2026-04-27 08:30:00');
INSERT INTO `sys_logininfor` VALUES (3,  'zhangsan', '192.168.1.10','深圳办公室', 'Edge 123',      'Windows 11',    '0', '登录成功', '2026-04-27 09:00:00');
INSERT INTO `sys_logininfor` VALUES (4,  'lisi',     '192.168.1.11','深圳办公室', 'Firefox 126',   'Windows 10',    '0', '登录成功', '2026-04-27 08:25:00');
INSERT INTO `sys_logininfor` VALUES (5,  'qianqi',   '192.168.1.14','深圳办公室', 'Chrome 125',    'macOS 15',       '0', '登录成功', '2026-04-27 08:45:00');
INSERT INTO `sys_logininfor` VALUES (6,  'zhoujiu',  '192.168.2.10','长沙分公司', 'Safari 18',      'macOS 15',       '0', '登录成功', '2026-04-27 08:10:00');
INSERT INTO `sys_logininfor` VALUES (7,  'admin',    '10.0.0.5',    'VPN网络',    'Chrome 125',    'Linux',          '0', '登录成功', '2026-04-26 22:30:00');

-- 失败登录 - 密码错误
INSERT INTO `sys_logininfor` VALUES (8,  'admin',    '127.0.0.1',  '本地',      'Chrome 125',    'Windows 11',    '1', '用户名或密码错误', '2026-04-27 08:57:00');
INSERT INTO `sys_logininfor` VALUES (9,  'admin',    '127.0.0.1',  '本地',      'Chrome 125',    'Windows 11',    '1', '用户名或密码错误', '2026-04-27 08:57:30');
INSERT INTO `sys_logininfor` VALUES (10, 'zhangsan', '192.168.1.99','未知IP',    'PostmanRuntime', 'Windows 11',    '1', '用户名或密码错误', '2026-04-27 07:00:00');

-- 失败登录 - 验证码错误
INSERT INTO `sys_logininfor` VALUES (11, 'admin',    '127.0.0.1',  '本地',      'Chrome 125',    'Windows 11',    '1', '验证码错误', '2026-04-27 08:56:00');

-- 失败登录 - 账号已停用
INSERT INTO `sys_logininfor` VALUES (12, 'disabled_user','127.0.0.1','本地',    'Chrome 125',    'Windows 11',    '1', '账号已停用，请联系管理员', '2026-04-27 09:10:00');

-- 失败登录 - 账号不存在
INSERT INTO `sys_logininfor` VALUES (13, 'nonexist', '127.0.0.1',  '本地',      'Chrome 125',    'Windows 11',    '1', '用户名或密码错误', '2026-04-27 09:15:00');

-- 失败登录 - 已删除账号
INSERT INTO `sys_logininfor` VALUES (14, 'deleted_user','127.0.0.1','本地',     'Chrome 125',    'Windows 11',    '1', '用户名或密码错误', '2026-04-27 09:20:00');

-- ============================================================
-- 数据统计汇总
-- ============================================================
--
-- 【用户表 sys_user】共 13 条
--   - 正常用户: 10 条（user_id 1~10，覆盖男/女/未知性别，4个部门，4种角色）
--   - 停用用户: 1 条（user_id=11, status='1'）
--   - 已删除用户: 1 条（user_id=12, del_flag='2'）
--   - 无部门用户: 1 条（user_id=13, dept_id=NULL）
--
-- 【角色表 sys_role】共 6 条
--   - 正常角色: 5 条（超级管理员/普通角色/部门管理员/只读/业务员）
--   - 停用角色: 1 条（role_id=6, status='1'）
--
-- 【部门表 sys_dept】共 12 条（原有5条 + 新增7条）
--   - 3级树形结构（总公司->分公司->部门->小组）
--   - 停用部门: 1 条（dept_id=112）
--
-- 【供应商表 sys_supplier】共 14 条
--   - 未联系(0): 3 条
--   - 已加微信(1): 3 条
--   - 未接(2): 2 条
--   - 空号(3): 2 条
--   - 已下单(4): 2 条
--   - 多品类: 1 条
--   - 已删除: 1 条（del_flag='2'）
--
-- 【仓库表 wms_warehouse】共 8 条
--   - 自有仓库(0): 4 条（含1条停用）
--   - 租赁仓库(1): 3 条（含1条已删除）
--   - 停用仓库: 1 条
--   - 已删除仓库: 1 条
--
-- 【商品表 wms_product】共 15 条
--   - 上架(0): 12 条
--   - 下架(1): 2 条
--   - 已删除(2): 1 条
--
-- 【商品供应商关联 wms_product_supplier】共 14 条
--   - 默认供应商: 9 条（is_default='1'）
--   - 非默认: 5 条
--   - 覆盖7种商品的多供应商关系
--
-- 【商品库存 wms_product_inventory】共 25 条
--   - 充足库存: 18 条（stock > warning_quantity）
--   - 接近预警: 1 条（stock 接近 warning）
--   - 缺货: 2 条（stock < warning_quantity）
--   - 分布在5个仓库
--
-- 【操作日志 sys_oper_log】共 17 条
--   - 正常操作: 15 条
--   - 异常操作: 2 条（含参数校验失败）
--   - 覆盖用户/角色/供应商/仓库/商品/导入导出
--
-- 【登录日志 sys_logininfor】共 14 条
--   - 成功: 7 条（多用户/多IP/多终端）
--   - 失败: 7 条（密码错误/验证码错误/停用/不存在/已删除）
--

-- ============================================================
-- 十一、电商模块测试数据
-- 覆盖：电商客户用户、商品、库存、购物车、订单（全状态）
-- ============================================================

-- -------------------- 1. 电商客户用户 --------------------
-- customer1 / 密码 Admin@2026 — 张小明
-- customer2 / 密码 Admin@2026 — 李小红
-- 密码统一使用 BCrypt 加密的 "Admin@2026"
INSERT INTO `sys_user` VALUES (101, 100, 'customer1', '张小明', '00', 'customer1@test.com', '13800001001', '0', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '', NULL, 1, sysdate(), NULL, NULL, '电商测试客户1');
INSERT INTO `sys_user` VALUES (102, 100, 'customer2', '李小红', '00', 'customer2@test.com', '13800001002', '1', '', '$2a$10$22BUeamzvjXTPmgwFVMQZedEJFW41hRtWsvbtHUzDjt5V9OpOd.N2', '0', '0', '', NULL, 1, sysdate(), NULL, NULL, '电商测试客户2');

-- 分配普通角色 (roleId=2)
INSERT INTO `sys_user_role` VALUES (101, 2);
INSERT INTO `sys_user_role` VALUES (102, 2);

-- -------------------- 2. 电商测试商品（product_id 101-120） --------------------

-- 数码电子类
INSERT INTO `wms_product` VALUES (101, 'iPhone 16 Pro Max 256GB', 'PHONE-IP16PM-256', '苹果最新旗舰手机，A18 Pro芯片，钛金属边框，4800万像素摄像头', 'https://picsum.photos/seed/iphone16/400/400', '<h3>iPhone 16 Pro Max</h3><p>全新设计，更强性能</p><ul><li>A18 Pro仿生芯片</li><li>6.9英寸超视网膜XDR显示屏</li><li>4800万像素主摄</li><li>钛金属设计</li></ul>', 9999.00, 7800.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '旗舰手机');
INSERT INTO `wms_product` VALUES (102, 'MacBook Air M3 13英寸', 'LAPTOP-MBA-M3-13', '轻薄笔记本电脑，M3芯片，18小时续航，Liquid Retina显示屏', 'https://picsum.photos/seed/macbook/400/400', '<h3>MacBook Air M3</h3><p>轻盈强大，随身携带</p><ul><li>M3芯片</li><li>8核CPU+10核GPU</li><li>18小时电池续航</li><li>仅1.24kg</li></ul>', 8999.00, 7200.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '热销笔记本');
INSERT INTO `wms_product` VALUES (103, '索尼 WH-1000XM5 头戴式耳机', 'HEADPHONE-SONY-XM5', '行业领先降噪，30小时续航，LDAC高品质音频传输', 'https://picsum.photos/seed/sonyxm5/400/400', '<h3>Sony WH-1000XM5</h3><p>静享纯粹音质</p><ul><li>行业领先降噪</li><li>30小时续航</li><li>LDAC/DSEE Extreme</li></ul>', 2499.00, 1800.00, '个', '0', '0', 1, sysdate(), NULL, NULL, '降噪耳机');
INSERT INTO `wms_product` VALUES (104, 'iPad Air M2 11英寸 256GB', 'TABLET-IPAD-AIR-M2', 'M2芯片驱动，Liquid Retina显示屏，支持Apple Pencil Pro', 'https://picsum.photos/seed/ipadair/400/400', '<h3>iPad Air M2</h3><p>创造力新体验</p><ul><li>M2芯片</li><li>11英寸Liquid Retina</li><li>支持Apple Pencil Pro</li></ul>', 4799.00, 3800.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '平板电脑');
INSERT INTO `wms_product` VALUES (105, 'AirPods Pro 2 Type-C版', 'EARBUDS-APP2-USBC', '自适应降噪，个性化空间音频，USB-C充电，6小时续航', 'https://picsum.photos/seed/airpods/400/400', '<h3>AirPods Pro 2</h3><ul><li>自适应降噪</li><li>个性化空间音频</li><li>USB-C充电</li><li>IP54防水</li></ul>', 1799.00, 1350.00, '个', '0', '0', 1, sysdate(), NULL, NULL, '无线耳机');

-- 服饰鞋包类
INSERT INTO `wms_product` VALUES (106, 'Nike Air Force 1 经典白色', 'SHOE-NIKE-AF1-WHT', '经典款运动鞋，全白配色，百搭潮流必备', 'https://picsum.photos/seed/nikeaf1/400/400', '<h3>Nike Air Force 1</h3><p>经典永不过时</p><ul><li>全粒面皮革鞋面</li><li>Air-Sole气垫</li><li>耐磨橡胶外底</li></ul>', 799.00, 420.00, '双', '0', '0', 1, sysdate(), NULL, NULL, '经典运动鞋');
INSERT INTO `wms_product` VALUES (107, 'Levi''s 501 经典直筒牛仔裤', 'JEAN-LEVIS-501-BLU', '经典原版剪裁，100%纯棉丹宁面料，纽扣门襟', 'https://picsum.photos/seed/levis501/400/400', '<h3>Levi''s 501 Original</h3><p>牛仔经典，始于1873</p><ul><li>100%纯棉</li><li>经典直筒版型</li><li>纽扣门襟设计</li></ul>', 699.00, 350.00, '条', '0', '0', 1, sysdate(), NULL, NULL, '经典牛仔裤');
INSERT INTO `wms_product` VALUES (108, 'Samsonite 新秀丽商务双肩包', 'BAG-SAMS-BP-BLK', '商务通勤双肩包，防泼水面料，15.6英寸笔记本隔层', 'https://picsum.photos/seed/samsonite/400/400', '<h3>Samsonite商务背包</h3><ul><li>防泼水面料</li><li>15.6寸笔记本隔层</li><li>多功能分区</li><li>减压背负系统</li></ul>', 599.00, 280.00, '个', '0', '0', 1, sysdate(), NULL, NULL, '商务背包');

-- 家居生活类
INSERT INTO `wms_product` VALUES (109, '戴森 V15 Detect 无绳吸尘器', 'HOME-DYSON-V15', '激光探测灰尘，智能吸力调节，60分钟续航', 'https://picsum.photos/seed/dysonv15/400/400', '<h3>Dyson V15 Detect</h3><p>看见看不见的灰尘</p><ul><li>激光灰尘探测</li><li>智能吸力调节</li><li>LCD实时显示</li><li>60分钟续航</li></ul>', 4490.00, 3200.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '旗舰吸尘器');
INSERT INTO `wms_product` VALUES (110, '小米空气净化器 4 Pro', 'HOME-MI-AIR4PRO', 'CADR值500m³/h，OLED触控屏，支持米家APP控制', 'https://picsum.photos/seed/miair/400/400', '<h3>小米空气净化器4 Pro</h3><ul><li>CADR 500m³/h</li><li>OLED触控屏</li><li>支持米家联动</li><li>静音模式33dB</li></ul>', 1499.00, 900.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '空气净化');
INSERT INTO `wms_product` VALUES (111, 'MUJI 超声波香薰机', 'HOME-MUJI-AROMA', '超声波雾化，两段光线调节，自动关机功能', 'https://picsum.photos/seed/mujiaroma/400/400', '<h3>MUJI 超声波香薰机</h3><ul><li>超声波雾化技术</li><li>两段灯光</li><li>静音运行</li><li>自动关机</li></ul>', 380.00, 180.00, '个', '0', '0', 1, sysdate(), NULL, NULL, '香薰机');

-- 美妆护肤类
INSERT INTO `wms_product` VALUES (112, 'SK-II 神仙水 230ml', 'BEAUTY-SKII-FTE-230', '90%以上PITERA精华，改善肤质，提亮肤色', 'https://picsum.photos/seed/sk2fte/400/400', '<h3>SK-II 护肤精华露</h3><p>改写肌肤命运</p><ul><li>90%+PITERA精华</li><li>改善肤质</li><li>提亮肤色</li><li>细腻毛孔</li></ul>', 1540.00, 980.00, '瓶', '0', '0', 1, sysdate(), NULL, NULL, '明星单品');
INSERT INTO `wms_product` VALUES (113, '兰蔻小黑瓶精华液 50ml', 'BEAUTY-LANCOME-AGS-50', '第二代小黑瓶，微生态科技，修护肌肤屏障', 'https://picsum.photos/seed/lancome/400/400', '<h3>兰蔻小黑瓶精华</h3><ul><li>微生态护肤科技</li><li>修护肌肤屏障</li><li>提升肌肤光泽</li></ul>', 1080.00, 650.00, '瓶', '0', '0', 1, sysdate(), NULL, NULL, '精华液');

-- 食品饮品类
INSERT INTO `wms_product` VALUES (114, '三顿半精品速溶咖啡 24颗装', 'FOOD-3HALF-COFFEE-24', '超即溶冷萃咖啡，3秒速溶，多风味混合装', 'https://picsum.photos/seed/coffee3half/400/400', '<h3>三顿半超即溶咖啡</h3><ul><li>冷萃工艺</li><li>3秒速溶</li><li>0糖0脂</li><li>多种风味</li></ul>', 169.00, 85.00, '盒', '0', '0', 1, sysdate(), NULL, NULL, '精品咖啡');
INSERT INTO `wms_product` VALUES (115, '农夫山泉东方树叶 500ml*15瓶', 'FOOD-NFS-TEA-15', '0糖0卡0脂，纯茶萃取，多口味混合装', 'https://picsum.photos/seed/dongfang/400/400', '<h3>东方树叶 混合装</h3><ul><li>0糖0卡0脂</li><li>茉莉花茶/乌龙茶/红茶</li><li>纯茶萃取</li></ul>', 75.00, 42.00, '箱', '0', '0', 1, sysdate(), NULL, NULL, '无糖茶饮');

-- 运动户外类
INSERT INTO `wms_product` VALUES (116, 'Keep 智能动感单车 C1', 'SPORT-KEEP-BIKE-C1', '家用静音磁控，支持Keep APP课程，阻力32档调节', 'https://picsum.photos/seed/keepbike/400/400', '<h3>Keep C1 动感单车</h3><ul><li>磁控静音</li><li>32档阻力</li><li>Keep APP联动</li><li>承重120kg</li></ul>', 1999.00, 1200.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '居家健身');
INSERT INTO `wms_product` VALUES (117, 'Yonex 尤尼克斯羽毛球拍 ARC11', 'SPORT-YNX-ARC11', '弓箭11经典款，高弹碳素，适合进阶选手', 'https://picsum.photos/seed/yonex/400/400', '<h3>Yonex ARC-11</h3><ul><li>高弹碳素框架</li><li>T型接头技术</li><li>进阶级推荐</li></ul>', 1080.00, 620.00, '支', '0', '0', 1, sysdate(), NULL, NULL, '羽毛球拍');

-- 图书文具类
INSERT INTO `wms_product` VALUES (118, '得力办公文具套装 15件', 'OFFICE-DELI-SET15', '包含订书机、计算器、剪刀、胶带等15件常用办公文具', 'https://picsum.photos/seed/delioffice/400/400', '<h3>得力办公套装</h3><ul><li>15件实用文具</li><li>订书机/计算器/剪刀</li><li>送礼盒包装</li></ul>', 89.00, 42.00, '套', '0', '0', 1, sysdate(), NULL, NULL, '办公套装');
INSERT INTO `wms_product` VALUES (119, 'Kindle Paperwhite 5 电子书阅读器', 'EBOOK-KINDLE-PW5', '6.8英寸E-Ink屏，冷暖调光，IPX8防水，32GB存储', 'https://picsum.photos/seed/kindle/400/400', '<h3>Kindle Paperwhite 5</h3><ul><li>6.8寸E-Ink屏</li><li>冷暖光调节</li><li>IPX8级防水</li><li>10周续航</li></ul>', 1068.00, 780.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '电子阅读');
INSERT INTO `wms_product` VALUES (120, '猫王小王子 OTR 便携蓝牙音箱', 'AUDIO-MWANG-OTR', '复古设计，锌合金机身，蓝牙5.0，10小时续航', 'https://picsum.photos/seed/catking/400/400', '<h3>猫王小王子OTR</h3><ul><li>复古经典设计</li><li>锌合金机身</li><li>蓝牙5.0</li><li>10小时续航</li></ul>', 399.00, 210.00, '台', '0', '0', 1, sysdate(), NULL, NULL, '复古音箱');

-- -------------------- 3. 新商品库存（inventory_id 201-235） --------------------
INSERT INTO `wms_product_inventory` VALUES (201, 101, 1, 50, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (202, 101, 3, 30, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (203, 102, 1, 40, 8, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (204, 102, 4, 25, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (205, 103, 1, 100, 15, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (206, 103, 2, 60, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (207, 104, 1, 35, 8, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (208, 104, 3, 20, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (209, 105, 1, 200, 30, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (210, 105, 2, 80, 15, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (211, 106, 1, 150, 20, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (212, 106, 4, 80, 15, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (213, 107, 2, 120, 20, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (214, 107, 3, 60, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (215, 108, 1, 90, 15, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (216, 109, 1, 25, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (217, 109, 4, 15, 3, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (218, 110, 1, 80, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (219, 110, 2, 50, 8, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (220, 111, 1, 200, 30, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (221, 112, 1, 60, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (222, 112, 4, 40, 8, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (223, 113, 1, 70, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (224, 114, 1, 300, 50, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (225, 114, 2, 200, 30, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (226, 115, 1, 500, 80, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (227, 115, 2, 300, 50, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (228, 116, 1, 20, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (229, 117, 1, 45, 8, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (230, 117, 3, 30, 5, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (231, 118, 1, 400, 60, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (232, 118, 2, 250, 40, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (233, 119, 1, 55, 10, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (234, 120, 1, 100, 15, '0', 1, sysdate(), NULL, NULL, NULL);
INSERT INTO `wms_product_inventory` VALUES (235, 120, 2, 60, 10, '0', 1, sysdate(), NULL, NULL, NULL);

-- -------------------- 4. 购物车测试数据 --------------------
-- customer1 的购物车（3件商品）
INSERT INTO `ecommerce_cart` VALUES (1, 101, 101, 1, sysdate(), sysdate());
INSERT INTO `ecommerce_cart` VALUES (2, 101, 106, 2, sysdate(), sysdate());
INSERT INTO `ecommerce_cart` VALUES (3, 101, 114, 3, sysdate(), sysdate());

-- customer2 的购物车（2件商品）
INSERT INTO `ecommerce_cart` VALUES (4, 102, 103, 1, sysdate(), sysdate());
INSERT INTO `ecommerce_cart` VALUES (5, 102, 112, 1, sysdate(), sysdate());

-- -------------------- 5. 订单测试数据（全状态覆盖） --------------------
-- customer1 的订单
-- 订单1: 已完成 (status=3)
INSERT INTO `ecommerce_order` VALUES (1, '20260401100000000001', 101, 1598.00, 3, '张小明', '13800001001', '广东省深圳市南山区科技园路1号', '', sysdate() - INTERVAL 20 DAY, sysdate() - INTERVAL 15 DAY);
INSERT INTO `ecommerce_order_item` VALUES (1, 1, 106, 'Nike Air Force 1 经典白色', 799.00, 2, 1598.00);

-- 订单2: 已发货 (status=2)
INSERT INTO `ecommerce_order` VALUES (2, '20260415140000000002', 101, 2499.00, 2, '张小明', '13800001001', '广东省深圳市南山区科技园路1号', '请工作日送货', sysdate() - INTERVAL 5 DAY, sysdate() - INTERVAL 3 DAY);
INSERT INTO `ecommerce_order_item` VALUES (2, 2, 103, '索尼 WH-1000XM5 头戴式耳机', 2499.00, 1, 2499.00);

-- 订单3: 待付款 (status=0)
INSERT INTO `ecommerce_order` VALUES (3, '20260427090000000003', 101, 9168.00, 0, '张小明', '13800001001', '广东省深圳市南山区科技园路1号', '', sysdate(), sysdate());
INSERT INTO `ecommerce_order_item` VALUES (3, 3, 102, 'MacBook Air M3 13英寸', 8999.00, 1, 8999.00);
INSERT INTO `ecommerce_order_item` VALUES (4, 3, 114, '三顿半精品速溶咖啡 24颗装', 169.00, 1, 169.00);

-- 订单4: 已取消 (status=4)
INSERT INTO `ecommerce_order` VALUES (4, '20260410080000000004', 101, 4490.00, 4, '张小明', '13800001001', '广东省深圳市南山区科技园路1号', '', sysdate() - INTERVAL 17 DAY, sysdate() - INTERVAL 16 DAY);
INSERT INTO `ecommerce_order_item` VALUES (5, 4, 109, '戴森 V15 Detect 无绳吸尘器', 4490.00, 1, 4490.00);

-- customer2 的订单
-- 订单5: 已付款 (status=1)
INSERT INTO `ecommerce_order` VALUES (5, '20260425160000000005', 102, 2620.00, 1, '李小红', '13800001002', '湖南省长沙市岳麓区麓谷大道88号', '尽快发货', sysdate() - INTERVAL 2 DAY, sysdate() - INTERVAL 1 DAY);
INSERT INTO `ecommerce_order_item` VALUES (6, 5, 112, 'SK-II 神仙水 230ml', 1540.00, 1, 1540.00);
INSERT INTO `ecommerce_order_item` VALUES (7, 5, 113, '兰蔻小黑瓶精华液 50ml', 1080.00, 1, 1080.00);

-- 订单6: 已完成 (status=3)
INSERT INTO `ecommerce_order` VALUES (6, '20260320120000000006', 102, 1068.00, 3, '李小红', '13800001002', '湖南省长沙市岳麓区麓谷大道88号', '', sysdate() - INTERVAL 38 DAY, sysdate() - INTERVAL 30 DAY);
INSERT INTO `ecommerce_order_item` VALUES (8, 6, 119, 'Kindle Paperwhite 5 电子书阅读器', 1068.00, 1, 1068.00);

-- 订单7: 待付款 (status=0)
INSERT INTO `ecommerce_order` VALUES (7, '20260427153000000007', 102, 1999.00, 0, '李小红', '13800001002', '湖南省长沙市岳麓区麓谷大道88号', '', sysdate(), sysdate());
INSERT INTO `ecommerce_order_item` VALUES (9, 7, 116, 'Keep 智能动感单车 C1', 1999.00, 1, 1999.00);

-- ============================================================
-- 电商模块数据统计汇总
-- ============================================================
-- 【电商客户用户 sys_user】共 2 条（user_id 101~102）
-- 【电商商品 wms_product】共 20 条（product_id 101~120）
-- 【电商商品库存 wms_product_inventory】共 35 条（inventory_id 201~235）
-- 【购物车 ecommerce_cart】共 5 条
-- 【订单 ecommerce_order】共 7 条
--   - 待付款(0): 2 条（订单3、订单7）
--   - 已付款(1): 1 条（订单5）
--   - 已发货(2): 1 条（订单2）
--   - 已完成(3): 2 条（订单1、订单6）
--   - 已取消(4): 1 条（订单4）
-- 【订单项 ecommerce_order_item】共 9 条
--
