package com.example.agent.agent;

import com.example.agent.approval.ApprovalService;
import com.example.agent.approval.PendingAction;
import com.example.agent.rag.KnowledgeBaseService;
import com.example.agent.rag.KnowledgeChunk;
import com.example.agent.tool.ToolCallTrace;
import com.example.agent.tool.ToolRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentService {

    private final KnowledgeBaseService knowledgeBaseService;
    private final ToolRegistry toolRegistry;
    private final ApprovalService approvalService;

    public AgentService(KnowledgeBaseService knowledgeBaseService,
                        ToolRegistry toolRegistry,
                        ApprovalService approvalService) {
        this.knowledgeBaseService = knowledgeBaseService;
        this.toolRegistry = toolRegistry;
        this.approvalService = approvalService;
    }

    public AgentResponse handle(AgentRequest request) {
        String question = request.question();
        List<String> plan = plan(question);
        List<KnowledgeChunk> evidence = knowledgeBaseService.search(question, 3);
        List<ToolCallTrace> traces = new ArrayList<>();
        PendingAction pendingAction = null;

        Map<String, Object> activity = callTool(traces, "activity.lookup", Map.of("keyword", pickActivityKeyword(question)));
        if (containsAny(question, "ROI", "下降", "转化", "渠道", "归因", "CPA", "CAC")) {
            callTool(traces, "report.channelFunnel", Map.of("activityId", activity.get("activityId")));
        }
        if (containsAny(question, "人群", "流失", "高价值", "标签")) {
            callTool(traces, "segment.query", Map.of("segmentName", "高价值流失用户"));
        }
        if (containsAny(question, "发券", "优惠券", "补贴", "召回")) {
            Map<String, Object> coupon = callTool(traces, "coupon.policy", Map.of("couponName", "流失召回20元券"));
            pendingAction = approvalService.createPendingAction(
                    request.operatorId() == null ? "demo-operator" : request.operatorId(),
                    "BATCH_COUPON_ISSUE",
                    Map.of(
                            "activityId", activity.get("activityId"),
                            "couponId", coupon.get("couponId"),
                            "segment", "高价值流失用户",
                            "expectedUsers", 1260,
                            "riskLevel", "HIGH"
                    )
            );
        }

        String answer = composeAnswer(question, evidence, traces, pendingAction);
        return new AgentResponse(answer, plan, evidence, traces, pendingAction);
    }

    private List<String> plan(String question) {
        List<String> steps = new ArrayList<>();
        steps.add("识别运营问题意图和涉及的营销活动");
        steps.add("检索营销 SOP、归因口径、风控规则等知识片段");
        steps.add("调用活动、渠道漏斗、人群或优惠券工具获取业务数据");
        if (containsAny(question, "发券", "补贴", "预算", "上下线")) {
            steps.add("生成高风险待审批动作，等待人工确认后执行");
        } else {
            steps.add("汇总结论、风险点和可执行优化建议");
        }
        return steps;
    }

    private Map<String, Object> callTool(List<ToolCallTrace> traces, String toolName, Map<String, Object> args) {
        long start = System.currentTimeMillis();
        Map<String, Object> result = toolRegistry.call(toolName, args);
        traces.add(new ToolCallTrace(toolName, args, result, System.currentTimeMillis() - start, "SUCCESS"));
        return result;
    }

    private String composeAnswer(String question,
                                 List<KnowledgeChunk> evidence,
                                 List<ToolCallTrace> traces,
                                 PendingAction pendingAction) {
        Map<String, Object> activity = findResult(traces, "activity.lookup");
        Map<String, Object> funnel = findResult(traces, "report.channelFunnel");
        StringBuilder answer = new StringBuilder();
        answer.append("我按企业 Agent 的方式处理了这个问题：先检索知识库，再调用业务工具，最后给出可审计结论。\n\n");
        answer.append("活动判断：").append(activity.getOrDefault("name", "未识别活动"))
                .append("，当前状态为 ").append(activity.getOrDefault("status", "UNKNOWN")).append("。\n");

        if (!funnel.isEmpty()) {
            answer.append("渠道诊断：整体 ROI 为 ").append(funnel.get("roi"))
                    .append("，CPA 为 ").append(funnel.get("cpa"))
                    .append("，主要问题集中在 ").append(funnel.get("weakPoint")).append("。\n");
            answer.append("建议：优先检查低转化渠道的落地页承接、券门槛、人群匹配和转化回传延迟；对高点击低支付渠道降低预算或更换权益策略。\n");
        }

        if (pendingAction != null) {
            answer.append("我没有直接执行发券，因为这是高风险写操作。已生成待审批动作 ")
                    .append(pendingAction.actionId())
                    .append("，需要人工确认后才会执行。\n");
        }

        if (!evidence.isEmpty()) {
            answer.append("参考知识：").append(evidence.get(0).title()).append("。\n");
        }
        answer.append("\n这个 Demo 的重点是让你理解：Agent 的价值不在聊天，而在把后端能力组织成可控工具链。");
        return answer.toString();
    }

    private Map<String, Object> findResult(List<ToolCallTrace> traces, String toolName) {
        return traces.stream()
                .filter(trace -> trace.toolName().equals(toolName))
                .findFirst()
                .map(ToolCallTrace::result)
                .orElseGet(LinkedHashMap::new);
    }

    private String pickActivityKeyword(String question) {
        if (question.contains("秒杀")) {
            return "秒杀";
        }
        if (question.contains("新人") || question.contains("礼包")) {
            return "新人礼包";
        }
        return "营销活动";
    }

    private boolean containsAny(String text, String... keywords) {
        for (String keyword : keywords) {
            if (text.contains(keyword)) {
                return true;
            }
        }
        return false;
    }
}
