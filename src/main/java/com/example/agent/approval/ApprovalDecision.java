package com.example.agent.approval;

public record ApprovalDecision(
        boolean approved,
        String reviewer,
        String comment
) {
}
