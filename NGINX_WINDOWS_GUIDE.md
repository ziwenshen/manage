# Windows下Nginx配置指南

## 🪟 Windows环境下Nginx配置步骤

### 1. 下载和安装Nginx

1. 访问 http://nginx.org/en/download.html
2. 下载Windows版本的nginx
3. 解压到 `C:\nginx` 目录

### 2. 配置Nginx

1. 将 `nginx-ziwen.xin.conf` 文件复制到 `C:\nginx\conf\conf.d\` 目录
2. 编辑 `C:\nginx\conf\nginx.conf`，在http块中添加：
   ```
   include conf.d/*.conf;
   ```

### 3. 创建网站目录

```cmd
mkdir C:\nginx\html\ziwen.xin
```

### 4. 创建简单首页

在 `C:\nginx\html\ziwen.xin\index.html` 创建欢迎页面。

### 5. 启动Nginx

```cmd
# 进入nginx目录
cd C:\nginx

# 启动nginx
nginx.exe

# 重新加载配置
nginx.exe -s reload

# 停止nginx
nginx.exe -s stop
```

### 6. 设置为Windows服务 (可选)

下载 `nssm` 工具将nginx设置为Windows服务：

```cmd
# 下载nssm
# 访问 https://nssm.cc/download

# 安装服务
nssm install nginx C:\nginx\nginx.exe
nssm start nginx
```

## 🔧 配置文件路径对照

| 项目 | Linux路径 | Windows路径 |
|------|-----------|-------------|
| 配置文件 | `/etc/nginx/sites-available/ziwen.xin` | `C:\nginx\conf\conf.d\ziwen.xin.conf` |
| 网站目录 | `/var/www/ziwen.xin` | `C:\nginx\html\ziwen.xin` |
| 日志目录 | `/var/log/nginx/` | `C:\nginx\logs\` |
| 主配置 | `/etc/nginx/nginx.conf` | `C:\nginx\conf\nginx.conf` |

## 🌐 测试访问

配置完成后，可以通过以下地址访问：

- http://ziwen.xin (主页)
- http://ziwen.xin/api (API接口)
- http://www.ziwen.xin/api (带www的API)

## 📋 故障排除

### 常见问题：

1. **端口被占用**：检查80端口是否被其他程序占用
   ```cmd
   netstat -ano | findstr :80
   ```

2. **配置语法错误**：测试配置文件
   ```cmd
   nginx.exe -t
   ```

3. **权限问题**：以管理员身份运行命令提示符

4. **防火墙阻止**：确保Windows防火墙允许80和443端口