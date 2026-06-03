# 上传 GitHub 指南

如果本机已安装 Git，可以在项目目录执行：

```bash
git init
git add .
git commit -m "Add ecommerce marketing agent demo"
git branch -M main
git remote add origin https://github.com/<your-name>/ecommerce-marketing-agent-demo.git
git push -u origin main
```

GitHub 现在通常不支持账号密码直接推送代码，需要使用 Personal Access Token、SSH Key，或者 GitHub Desktop。

## 推荐方式

1. 在 GitHub 新建仓库：`ecommerce-marketing-agent-demo`
2. 安装 GitHub Desktop。
3. 选择 `Add existing repository`，目录选择本项目。
4. Commit 后点击 Publish repository。

## 不建议

- 不要把账号密码写进 README、脚本、`.env` 或命令历史。
- 不要在没有审批逻辑的情况下让 Agent 执行发券、预算调整、活动上下线等写操作。
