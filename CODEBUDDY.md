# CODEBUDDY.md

## 常用命令

### 后端 (Spring Boot)
```bash
# 编译打包
cd task-manager-backend && ./mvnw.cmd clean package -DskipTests

# 运行（需先启动 MySQL 和 Redis）
./mvnw.cmd spring-boot:run

# 运行测试
./mvnw.cmd test

# 运行单个测试类
./mvnw.cmd test -Dtest=BaseControllerTest

# 跳过测试编译
./mvnw.cmd clean compile
```

### 前端 (Vue 3)
```bash
cd task-manager-frontend

# 安装依赖
npm install

# 开发模式启动（代理到后端 /dev-api）
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
```

## 技术栈

**后端**: Spring Boot 3.2.0 + Java 17 + MyBatis-Plus 3.5.5 + Spring Security + Redis + MySQL
**前端**: Vue 3 + Vite + Element Plus + Pinia + Vue Router 4

## 架构概述

本项目是基于 RuoYi（若依）风格的前后端分离后台管理系统，采用标准的三层架构 + RBAC 权限控制。

### 后端分层

```
controller/    → REST API 入口，处理请求参数校验
domain/        → 业务实体类，与数据库表一一对应
mapper/        → MyBatis-Plus 数据访问层（含 XML SQL）
common/        → 通用组件：统一响应Result、分页封装、日志注解、异常处理
config/        → Spring Security、CORS、Redis、MyBatis-Plus 配置
security/      → JWT 过滤器、Token管理、用户详情服务、权限校验
aspect/        → AOP 切面：操作日志记录（@Log注解触发）
```

### 核心机制

**统一响应格式**: `Result<T>`（code/message/data），Controller 必须返回此格式
**分页数据**: `TableDataInfo<T>` 封装 MyBatis-Plus 分页结果（total/rows/pageNum/pageSize）
**逻辑删除**: del_flag='0' 存在，del_flag='2' 删除，无需物理删除
**权限控制**: `@PreAuthorize("@ss.hasPermi('system:user:list')")` 在方法级校验权限字符串
**操作日志**: 方法标注 `@Log(title="用户管理", businessType=INSERT/UPDATE/DELETE)` 自动记录到 sys_oper_log

### 前端分层

```
api/           → axios 封装，统一拦截器注入 Token
views/         → 页面组件，按模块组织（dashboard/error/monitor/system）
router/        → 静态路由定义，动态路由由后端菜单数据生成
permission.js  → 路由守卫，根据后端返回的菜单树动态挂载路由
utils/         → 工具函数（auth.ts 管理 Token）
```

### 认证流程

1. 前端 POST `/api/auth/login` 获取 Token（UUID格式）
2. 后端将 `LoginUser` 对象存入 Redis（Key前缀: `login_tokens:`）
3. 前端将 Token 存入 localStorage，后续请求 Header 携带 `Authorization: Bearer <token>`
4. `JwtAuthenticationFilter` 从 Redis 恢复用户信息，每次请求自动续期

### 数据库核心表

- `sys_user` — 用户信息（含密码 BCrypt 加密）
- `sys_role` — 角色定义（role_key 作为权限标识）
- `sys_menu` — 菜单权限树（path/component/perms/icon，支撑前端动态路由）
- `sys_user_role` — 用户角色关联
- `sys_role_menu` — 角色菜单关联
- `sys_dept` — 部门树（ancestors 字段存储祖级链）
- `sys_oper_log` — 操作日志（由 LogAspect 自动写入）

### API 路由约定

| 路径 | 说明 |
|------|------|
| `/api/auth/*` | 认证接口（登录/登出/验证码）公开 |
| `/api/system/*` | 系统管理模块（用户/角色/菜单/部门/字典）需认证 |
| `/doc.html` | Knife4j API 文档 |

### 前端代理配置

Vite 开发服务器代理 `/dev-api` 到 `http://localhost:8080`，API 调用无需修改 BASE_URL。

## 开发注意事项

- Controller 直接注入 Mapper，无 Service 层（参照 RuoYi 原生风格）
- Mapper XML 文件位于 `src/main/resources/mapper/`
- 新增 Controller 方法需添加 `@PreAuthorize` 权限注解，否则会被 Security 拦截
- 字典数据 `/api/system/dict/data/type/{dictType}` 公开访问
- 数据库迁移脚本：`src/main/resources/schema.sql`
