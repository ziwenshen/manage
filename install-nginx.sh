#!/bin/bash
# Nginx安装和配置脚本 - Ubuntu/CentOS
# 使用方法: chmod +x install-nginx.sh && sudo ./install-nginx.sh

echo "🚀 开始安装和配置Nginx..."

# 检测系统类型
if [ -f /etc/ubuntu-release ] || [ -f /etc/debian_version ]; then
    # Ubuntu/Debian系统
    echo "📦 检测到Ubuntu/Debian系统，开始安装Nginx..."
    
    # 更新软件包
    apt update
    
    # 安装Nginx
    apt install -y nginx
    
    # 配置目录
    NGINX_SITES_DIR="/etc/nginx/sites-available"
    NGINX_ENABLED_DIR="/etc/nginx/sites-enabled"
    
elif [ -f /etc/redhat-release ] || [ -f /etc/centos-release ]; then
    # CentOS/RHEL系统
    echo "📦 检测到CentOS/RHEL系统，开始安装Nginx..."
    
    # 安装EPEL仓库
    yum install -y epel-release
    
    # 安装Nginx
    yum install -y nginx
    
    # 配置目录
    NGINX_SITES_DIR="/etc/nginx/conf.d"
    NGINX_ENABLED_DIR="/etc/nginx/conf.d"
    
else
    echo "❌ 不支持的操作系统"
    exit 1
fi

echo "✅ Nginx安装完成"

# 创建网站目录
echo "📁 创建网站目录..."
mkdir -p /var/www/ziwen.xin
chown -R nginx:nginx /var/www/ziwen.xin 2>/dev/null || chown -R www-data:www-data /var/www/ziwen.xin

# 创建简单的欢迎页面
cat > /var/www/ziwen.xin/index.html << 'EOF'
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>ziwen.xin - 管理系统</title>
    <style>
        body { 
            font-family: 'Microsoft YaHei', Arial, sans-serif; 
            text-align: center; 
            margin-top: 100px; 
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            min-height: 100vh;
            margin: 0;
            display: flex;
            align-items: center;
            justify-content: center;
        }
        .container {
            background: rgba(255,255,255,0.1);
            padding: 40px;
            border-radius: 10px;
            backdrop-filter: blur(10px);
        }
        h1 { margin-bottom: 20px; }
        .api-link { 
            color: #ffeb3b; 
            text-decoration: none; 
            font-weight: bold;
            font-size: 18px;
        }
        .api-link:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <div class="container">
        <h1>🎉 欢迎访问 ziwen.xin</h1>
        <p>管理系统已成功部署！</p>
        <p>
            <a href="/api/" class="api-link">📡 访问API接口</a>
        </p>
        <p style="margin-top: 30px; font-size: 14px; opacity: 0.8;">
            Powered by Spring Boot + Nginx
        </p>
    </div>
</body>
</html>
EOF

# 复制Nginx配置
echo "⚙️ 配置Nginx..."
if [ "$NGINX_SITES_DIR" = "/etc/nginx/sites-available" ]; then
    # Ubuntu/Debian
    cp nginx-ziwen.xin.conf $NGINX_SITES_DIR/ziwen.xin
    ln -sf $NGINX_SITES_DIR/ziwen.xin $NGINX_ENABLED_DIR/
    # 删除默认站点
    rm -f $NGINX_ENABLED_DIR/default
else
    # CentOS/RHEL
    cp nginx-ziwen.xin.conf $NGINX_SITES_DIR/ziwen.xin.conf
fi

# 测试Nginx配置
echo "🔍 测试Nginx配置..."
nginx -t

if [ $? -eq 0 ]; then
    echo "✅ Nginx配置测试通过"
    
    # 启动并启用Nginx服务
    systemctl enable nginx
    systemctl start nginx
    systemctl reload nginx
    
    echo "🎉 Nginx配置完成！"
    echo ""
    echo "📝 后续步骤："
    echo "1. 确保Spring Boot应用在8080端口运行"
    echo "2. 访问 http://ziwen.xin 测试"
    echo "3. 如需HTTPS，请配置SSL证书"
    echo ""
    echo "🔗 访问地址："
    echo "   - http://ziwen.xin"
    echo "   - http://www.ziwen.xin"
    echo "   - http://ziwen.xin/api"
    
else
    echo "❌ Nginx配置测试失败，请检查配置文件"
    exit 1
fi