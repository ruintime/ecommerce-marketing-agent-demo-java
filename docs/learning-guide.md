# 学习路线

## 1. 先理解 Agent 和普通 Chatbot 的区别

普通 Chatbot 主要生成文本。企业级 Agent 至少要具备四件事：

1. 能理解任务目标。
2. 能检索业务知识。
3. 能调用后端工具。
4. 能对高风险动作做人工确认和审计。

本项目中对应代码：

- 任务规划：`AgentService.plan`
- RAG 检索：`KnowledgeBaseService.search`
- 工具调用：`ToolRegistry.call`
- 人工确认：`ApprovalService.createPendingAction`

## 2. 看一次完整链路

输入：

```text
为什么新人礼包活动 ROI 下降？
```

链路：

1. Agent 识别这是活动诊断问题。
2. 从知识库检索 ROI、CPA、CAC、归因口径。
3. 调用 `activity.lookup` 查询活动配置。
4. 调用 `report.channelFunnel` 查询渠道漏斗。
5. 组合回答，并展示工具调用日志。

## 3. 理解工具调用的工程问题

真实项目中，工具调用不能只是 HTTP 请求。你要考虑：

- 工具 schema 是否清晰。
- 参数是否可校验。
- 当前用户是否有权限。
- 写操作是否需要审批。
- 调用是否幂等。
- 失败是否能重试或补偿。
- 日志是否能追溯。

这些经验正好对应你过去做的 Java 后端、MQ、幂等、补偿、监控经验。

## 4. 如何接入真实 LLM

建议分三步：

1. 先把 `AgentService.plan` 换成 LLM 生成计划。
2. 再把 `composeAnswer` 换成 LLM 根据证据和工具结果生成答案。
3. 最后让 LLM 根据工具 schema 自主选择工具，但保留白名单、权限和审批。

不要一开始就让模型完全自由调用工具，这在生产系统里风险很高。

## 5. 如何改造成简历项目

可以写成：

基于 Java / Spring Boot 设计电商营销自动化 Agent Demo，将活动查询、渠道漏斗、人群包、优惠券策略等业务能力封装为 Agent 工具，结合 RAG 知识检索支持运营自然语言诊断活动 ROI、CPA、CAC 异常；针对批量发券等高风险动作设计人工确认、操作审计和待执行任务机制，体现企业级 Agent 的可控落地方式。
