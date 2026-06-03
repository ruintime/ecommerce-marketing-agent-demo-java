package com.example.agent.agent;

import com.example.agent.approval.PendingAction;
import com.example.agent.rag.KnowledgeChunk;
import com.example.agent.tool.ToolCallTrace;

import java.util.List;

public record AgentResponse(
        String answer,
        List<String> plan,
        List<KnowledgeChunk> evidence,
        List<ToolCallTrace> toolCalls,
        PendingAction pendingAction
) {
}
