package com.example.agent.api;

import com.example.agent.agent.AgentRequest;
import com.example.agent.agent.AgentResponse;
import com.example.agent.agent.AgentService;
import com.example.agent.approval.ApprovalDecision;
import com.example.agent.approval.ApprovalResult;
import com.example.agent.approval.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final AgentService agentService;
    private final ApprovalService approvalService;

    public AgentController(AgentService agentService, ApprovalService approvalService) {
        this.agentService = agentService;
        this.approvalService = approvalService;
    }

    @PostMapping("/chat")
    public AgentResponse chat(@Valid @RequestBody AgentRequest request) {
        return agentService.handle(request);
    }

    @GetMapping("/pending-actions")
    public List<ApprovalResult> pendingActions() {
        return approvalService.pendingActions();
    }

    @PostMapping("/approve/{actionId}")
    public ApprovalResult approve(@PathVariable String actionId, @RequestBody ApprovalDecision decision) {
        return approvalService.decide(actionId, decision);
    }
}
