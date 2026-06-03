package com.example.agent.approval;

import java.util.Map;

public record PendingAction(
        String actionId,
        String actionType,
        String operatorId,
        String status,
        Map<String, Object> payload,
        String createdAt
) {
}
