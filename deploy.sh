#!/bin/bash
# 一键部署脚本 - 完整部署Spring Boot应用 + Nginx
# 使用方法: chmod +x deploy.sh && sudo ./deploy.sh

set -e  # 遇到错误立即退出

echo "🚀 开始一键部署ziwen.xin管理系统..."

# 配置变量
DOMAIN="ziwen.xin"
APP_NAME="manage"
APP_PORT="8080"
JAVA_OPTS="-Xms512m -Xmx1024m -Dspring.profiles.active=prod"
APP_USER="manage"
SERVICE_NAME="manage-app"

# 检查是否为root用户
if [ "$EUID" -ne 0 ]; then 
    echo "❌ 请使用sudo运行此脚本"
    exit 1
fi

echo "📋 部署信息："
echo "   域名: $DOMAIN"
echo "   应用: $APP_NAME"
echo "   端口: $APP_PORT"
echo "   环境: 生产环境"
echo ""

# 1. 创建应用用户
echo "👤 创建应用用户..."
if ! id "$APP_USER" &>/dev/null; then
    useradd -r -s /bin/false -d /opt/$APP_NAME $APP_USER
    echo "✅ 用户 $APP_USER 创建成功"
else
    echo "✅ 用户 $APP_USER 已存在"
fi

# 2. 创建应用目录
echo "📁 创建应用目录..."
mkdir -p /opt/$APP_NAME/{bin,logs,config}
chown -R $APP_USER:$APP_USER /opt/$APP_NAME

# 3. 复制应用文件
echo "📦 复制应用文件..."
if [ -f "target/$APP_NAME.jar" ]; then
    cp target/$APP_NAME.jar /opt/$APP_NAME/bin/
    chown $APP_USER:$APP_USER /opt/$APP_NAME/bin/$APP_NAME.jar
    echo "✅ 应用JAR文件复制成功"
else
    echo "❌ 找不到target/$APP_NAME.jar，请先编译项目"
    echo "   运行: mvn clean package -Dmaven.test.skip=true"
    exit 1
fi

# 4. 创建systemd服务
echo "⚙️ 创建systemd服务..."
cat > /etc/systemd/system/$SERVICE_NAME.service << EOF
[Unit]
Description=Ziwen.xin Management System
After=network.target

[Service]
Type=simple
User=$APP_USER
WorkingDirectory=/opt/$APP_NAME
ExecStart=/usr/bin/java $JAVA_OPTS -jar /opt/$APP_NAME/bin/$APP_NAME.jar
ExecStop=/bin/kill -TERM \$MAINPID
Restart=always
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# 重新加载systemd
systemctl daemon-reload
systemctl enable $SERVICE_NAME

echo "✅ 系统服务创建成功"

# 5. 安装和配置Nginx
echo "🌐 安装和配置Nginx..."
./install-nginx.sh

# 6. 启动应用
echo "🚀 启动应用..."
systemctl start $SERVICE_NAME

# 等待应用启动
echo "⏳ 等待应用启动..."
for i in {1..30}; do
    if curl -f http://localhost:$APP_PORT/api/health >/dev/null 2>&1; then
        echo "✅ 应用启动成功"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ 应用启动失败，请检查日志"
        echo "   查看日志: journalctl -u $SERVICE_NAME -f"
        exit 1
    fi
    sleep 2
done

# 7. 测试访问
echo "🔍 测试访问..."
if curl -f http://localhost/api/ >/dev/null 2>&1; then
    echo "✅ Nginx代理配置成功"
else
    echo "⚠️ Nginx代理可能有问题，请检查配置"
fi

echo ""
echo "🎉 部署完成！"
echo ""
echo "📋 服务管理命令："
echo "   启动应用: sudo systemctl start $SERVICE_NAME"
echo "   停止应用: sudo systemctl stop $SERVICE_NAME"
echo "   重启应用: sudo systemctl restart $SERVICE_NAME"
echo "   查看状态: sudo systemctl status $SERVICE_NAME"
echo "   查看日志: sudo journalctl -u $SERVICE_NAME -f"
echo ""
echo "🔗 访问地址："
echo "   HTTP:  http://$DOMAIN"
echo "   API:   http://$DOMAIN/api"
echo ""
echo "📝 后续步骤（可选）："
echo "   1. 配置SSL证书: sudo ./setup-ssl.sh"
echo "   2. 配置防火墙: sudo ufw allow 80,443/tcp"
echo "   3. 监控应用状态"
echo ""
echo "🛠️ 故障排除："
echo "   应用日志: sudo journalctl -u $SERVICE_NAME -f"
echo "   Nginx日志: sudo tail -f /var/log/nginx/ziwen.xin.error.log"