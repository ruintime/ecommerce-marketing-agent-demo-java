package com.example.agent.agent;

import com.example.agent.approval.ApprovalService;
import com.example.agent.data.MarketingDataService;
import com.example.agent.rag.KnowledgeBaseService;
import com.example.agent.tool.ToolRegistry;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AgentServiceTest {

    @Test
    void shouldDiagnoseRoiQuestionWithToolCalls() {
        AgentService service = new AgentService(
                new KnowledgeBaseService(),
                new ToolRegistry(new MarketingDataService()),
                new ApprovalService()
        );

        AgentResponse response = service.handle(new AgentRequest("为什么新人礼包活动 ROI 下降？", "tester"));

        assertThat(response.answer()).contains("ROI");
        assertThat(response.toolCalls()).extracting("toolName")
                .contains("activity.lookup", "report.channelFunnel");
        assertThat(response.pendingAction()).isNull();
    }

    @Test
    void shouldCreatePendingActionForCouponIssue() {
        AgentService service = new AgentService(
                new KnowledgeBaseService(),
                new ToolRegistry(new MarketingDataService()),
                new ApprovalService()
        );

        AgentResponse response = service.handle(new AgentRequest("给高价值流失用户发一张 20 元优惠券", "tester"));

        assertThat(response.pendingAction()).isNotNull();
        assertThat(response.pendingAction().status()).isEqualTo("PENDING");
    }
}
