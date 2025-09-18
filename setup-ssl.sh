#!/bin/bash
# SSL证书获取脚本 - 使用Let's Encrypt免费SSL证书
# 使用方法: chmod +x setup-ssl.sh && sudo ./setup-ssl.sh

DOMAIN="ziwen.xin"
WWW_DOMAIN="www.ziwen.xin"
API_DOMAIN="api.ziwen.xin"
EMAIL="your-email@example.com"  # 请替换为您的邮箱

echo "🔒 开始配置SSL证书..."

# 检查系统类型
if [ -f /etc/ubuntu-release ] || [ -f /etc/debian_version ]; then
    # Ubuntu/Debian系统
    echo "📦 检测到Ubuntu/Debian系统"
    
    # 安装snapd
    apt update
    apt install -y snapd
    
    # 安装certbot
    snap install core; snap refresh core
    snap install --classic certbot
    
    # 创建符号链接
    ln -sf /snap/bin/certbot /usr/bin/certbot
    
elif [ -f /etc/redhat-release ] || [ -f /etc/centos-release ]; then
    # CentOS/RHEL系统
    echo "📦 检测到CentOS/RHEL系统"
    
    # 安装EPEL仓库
    yum install -y epel-release
    
    # 安装certbot
    yum install -y certbot python3-certbot-nginx
    
else
    echo "❌ 不支持的操作系统"
    exit 1
fi

echo "✅ Certbot安装完成"

# 获取SSL证书
echo "🔑 获取SSL证书..."

# 停止nginx以便certbot验证域名
systemctl stop nginx

# 使用standalone模式获取证书
certbot certonly --standalone \
    --email $EMAIL \
    --agree-tos \
    --no-eff-email \
    -d $DOMAIN \
    -d $WWW_DOMAIN \
    -d $API_DOMAIN

if [ $? -eq 0 ]; then
    echo "✅ SSL证书获取成功"
    
    # 更新nginx配置以启用SSL
    NGINX_CONF="/etc/nginx/sites-available/ziwen.xin"
    if [ ! -f "$NGINX_CONF" ]; then
        NGINX_CONF="/etc/nginx/conf.d/ziwen.xin.conf"
    fi
    
    # 备份原配置
    cp "$NGINX_CONF" "$NGINX_CONF.backup"
    
    # 替换SSL证书路径
    sed -i "s|# ssl_certificate|ssl_certificate|g" "$NGINX_CONF"
    sed -i "s|# ssl_certificate_key|ssl_certificate_key|g" "$NGINX_CONF"
    sed -i "s|/etc/nginx/ssl/ziwen.xin.crt|/etc/letsencrypt/live/$DOMAIN/fullchain.pem|g" "$NGINX_CONF"
    sed -i "s|/etc/nginx/ssl/ziwen.xin.key|/etc/letsencrypt/live/$DOMAIN/privkey.pem|g" "$NGINX_CONF"
    
    # 启用HTTP到HTTPS重定向
    sed -i 's|# server {|server {|g' "$NGINX_CONF"
    sed -i 's|#     listen 80;|    listen 80;|g' "$NGINX_CONF"
    sed -i 's|#     server_name|    server_name|g' "$NGINX_CONF"
    sed -i 's|#     return 301|    return 301|g' "$NGINX_CONF"
    sed -i 's|# }|}|g' "$NGINX_CONF"
    
    # 测试nginx配置
    nginx -t
    
    if [ $? -eq 0 ]; then
        # 启动nginx
        systemctl start nginx
        systemctl reload nginx
        
        # 设置证书自动续期
        echo "⏰ 设置SSL证书自动续期..."
        (crontab -l 2>/dev/null; echo "0 12 * * * /usr/bin/certbot renew --quiet --reload-hook 'systemctl reload nginx'") | crontab -
        
        echo "🎉 SSL配置完成！"
        echo ""
        echo "🔗 HTTPS访问地址："
        echo "   - https://ziwen.xin"
        echo "   - https://www.ziwen.xin"
        echo "   - https://api.ziwen.xin/api"
        echo ""
        echo "📅 证书将自动续期"
        
    else
        echo "❌ Nginx配置测试失败"
        # 恢复备份
        mv "$NGINX_CONF.backup" "$NGINX_CONF"
        systemctl start nginx
    fi
    
else
    echo "❌ SSL证书获取失败"
    echo "请检查："
    echo "1. 域名DNS解析是否正确"
    echo "2. 80端口是否开放"
    echo "3. 邮箱地址是否正确"
    
    # 启动nginx
    systemctl start nginx
fi

echo ""
echo "📋 SSL证书管理命令："
echo "   查看证书: certbot certificates"
echo "   续期证书: certbot renew"
echo "   删除证书: certbot delete"