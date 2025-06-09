# Java Login System

一个使用Java Swing和SQLite数据库实现的简单登录系统。

## 功能特性

- 用户注册和登录
- 密码加密存储（使用BCrypt）
- 输入验证
- 简洁的Swing GUI界面
- SQLite数据库存储
- MVC架构设计

## 技术栈

- **Java 11+**
- **Maven** - 项目构建工具
- **Swing** - GUI框架
- **SQLite** - 数据库
- **BCrypt** - 密码加密
- **JUnit** - 单元测试

## 项目结构

```
java-login-system/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── login/
│   │   │           ├── Main.java
│   │   │           ├── model/
│   │   │           │   └── User.java
│   │   │           ├── dao/
│   │   │           │   ├── UserDAO.java
│   │   │           │   └── impl/
│   │   │           │       └── UserDAOImpl.java
│   │   │           ├── service/
│   │   │           │   └── UserService.java
│   │   │           ├── view/
│   │   │           │   └── LoginFrame.java
│   │   │           └── util/
│   │   │               ├── DatabaseUtil.java
│   │   │               └── PasswordUtil.java
│   │   └── resources/
│   │       └── database.properties
│   └── test/
│       └── java/
│           └── com/
│               └── login/
│                   └── service/
│                       └── UserServiceTest.java
```

## 安装和运行

### 前提条件

- Java 11 或更高版本
- Maven 3.6 或更高版本

### 步骤

1. **克隆或下载项目**
   ```bash
   cd java-login-system
   ```

2. **编译项目**
   ```bash
   mvn clean compile
   ```

3. **运行应用程序**
   ```bash
   mvn exec:java -Dexec.mainClass="com.login.Main"
   ```

   或者先打包再运行：
   ```bash
   mvn clean package
   java -jar target/java-login-system-1.0.0.jar
   ```

4. **运行测试**
   ```bash
   mvn test
   ```

## 使用说明

### 注册新用户

1. 启动应用程序
2. 点击"Switch to Register"按钮
3. 填写用户名、密码和邮箱（可选）
4. 点击"Register"按钮

### 登录

1. 在登录界面输入用户名和密码
2. 点击"Login"按钮
3. 登录成功后会显示欢迎界面

### 密码要求

- 至少6个字符
- 包含至少一个字母和一个数字

### 用户名要求

- 至少3个字符
- 最多50个字符
- 必须唯一

## 数据库配置

默认使用SQLite数据库，数据库文件会自动创建在项目根目录下。

如需使用其他数据库，请修改 `src/main/resources/database.properties` 文件：

```properties
# MySQL示例
db.url=jdbc:mysql://localhost:3306/login_system
db.user=your_username
db.password=your_password

# PostgreSQL示例
db.url=jdbc:postgresql://localhost:5432/login_system
db.user=your_username
db.password=your_password
```

## 架构说明

### MVC架构

- **Model** (`com.login.model`): 数据模型类
- **View** (`com.login.view`): Swing GUI界面
- **Controller** (`com.login.service`): 业务逻辑层

### 数据访问层

- **DAO接口** (`com.login.dao`): 数据访问对象接口
- **DAO实现** (`com.login.dao.impl`): 具体的数据库操作实现

### 工具类

- **DatabaseUtil**: 数据库连接和初始化
- **PasswordUtil**: 密码加密和验证

## 安全特性

- 密码使用BCrypt加密存储
- SQL注入防护（使用PreparedStatement）
- 输入验证和清理
- 密码强度验证

## 扩展功能

可以考虑添加以下功能：

- 忘记密码功能
- 用户角色和权限管理
- 登录日志记录
- 账户锁定机制
- 更复杂的密码策略
- 邮箱验证
- 双因素认证

## 故障排除

### 常见问题

1. **数据库连接失败**
   - 检查数据库配置文件
   - 确保有写入权限

2. **编译错误**
   - 检查Java版本（需要11+）
   - 运行 `mvn clean` 清理项目

3. **GUI显示问题**
   - 确保运行在支持GUI的环境中
   - 检查系统的Look and Feel设置

## 许可证

本项目仅供学习和演示使用。

## 贡献

欢迎提交问题报告和改进建议。
