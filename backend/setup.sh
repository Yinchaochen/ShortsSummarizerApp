#!/bin/bash
# 在 WSL 里运行这个脚本来启动 Redis
sudo apt-get update -qq
sudo apt-get install -y redis-server
redis-server --daemonize yes
echo "Redis started on localhost:6379"
