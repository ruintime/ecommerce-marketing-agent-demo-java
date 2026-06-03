# 电商营销自动化 Agent 平台 Demo

这是一个给 Java 后端转 Agent 开发方向学习用的 Demo。它不直接调用真实大模型，先用可读、可调试的 Java 代码模拟企业级 Agent 的核心链路：

- RAG 知识库检索：从营销 SOP、归因口径、风控规则中找证据。
- Tool Calling：调用活动查询、渠道报表、用户分群、优惠券等业务工具。
- 工作流编排：根据问题规划查询步骤，输出诊断结论和建议。
- 人工确认：高风险动作如批量发券不会直接执行，需要审批。
- 可观测：返回每一步工具调用、参数、耗时和证据来源。

## 适合学习什么

这个项目对应真实 Agent 岗位常见要求：

- Java / Spring Boot 如何承接 AI 应用后端。
- Agent 不是聊天机器人，而是“理解任务 -> 检索知识 -> 调用工具 -> 生成结论 -> 人工确认执行”。
- 企业系统中要关注权限、幂等、审计、超时、重试、成本和安全。

## 运行方式

本机需要安装 Java 17 和 Maven。

```bash
mvn spring-boot:run
```

打开：

```text
http://localhost:8080
```

## 推荐提问

```text
为什么新人礼包活动 ROI 下降？
```

```text
查询 618 秒杀活动的渠道转化情况
```

```text
给高价值流失用户发一张 20 元优惠券
```

第三个问题会生成“待审批动作”，不会直接执行发券。

## 后续如何接真实大模型

建议先看懂 `AgentService` 的编排逻辑，再替换其中的 `plan()` 和 `composeAnswer()`：

1. 用 Spring AI 或 LangChain4j 接入 OpenAI、DeepSeek、通义等模型。
2. 把 `KnowledgeBaseService.search()` 换成真正的向量库检索。
3. 把 `MarketingDataService` 换成真实业务服务或 MCP 工具。
4. 加入评测集，验证检索命中率、工具调用准确率和回答准确率。

详细学习路线见 [docs/learning-guide.md](docs/learning-guide.md)。
