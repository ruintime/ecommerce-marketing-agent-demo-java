package com.example.agent.approval;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ApprovalService {

    private final Map<String, ApprovalResult> actions = new LinkedHashMap<>();

    public PendingAction createPendingAction(String operatorId, String actionType, Map<String, Object> payload) {
        PendingAction action = new PendingAction(
                "act-" + UUID.randomUUID().toString().substring(0, 8),
                actionType,
                operatorId,
                "PENDING",
                payload,
                Instant.now().toString()
        );
        actions.put(action.actionId(), new ApprovalResult(action.actionId(), "PENDING", action, "等待人工审批"));
        return action;
    }

    public List<ApprovalResult> pendingActions() {
        return new ArrayList<>(actions.values());
    }

    public ApprovalResult decide(String actionId, ApprovalDecision decision) {
        ApprovalResult old = actions.get(actionId);
        if (old == null) {
            return new ApprovalResult(actionId, "NOT_FOUND", null, "待审批动作不存在");
        }
        String status = decision.approved() ? "APPROVED" : "REJECTED";
        PendingAction updated = new PendingAction(
                old.action().actionId(),
                old.action().actionType(),
                old.action().operatorId(),
                status,
                old.action().payload(),
                old.action().createdAt()
        );
        ApprovalResult result = new ApprovalResult(
                actionId,
                status,
                updated,
                decision.approved() ? "审批通过：这里会进入真实发券任务队列" : "审批拒绝：不会执行写操作"
        );
        actions.put(actionId, result);
        return result;
    }
}
