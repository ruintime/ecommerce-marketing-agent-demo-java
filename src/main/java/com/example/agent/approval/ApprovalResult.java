package com.example.agent.approval;

public record ApprovalResult(
        String actionId,
        String status,
        PendingAction action,
        String message
) {
}
