package com.example.agent.tool;

import java.util.Map;

public record ToolCallTrace(
        String toolName,
        Map<String, Object> arguments,
        Map<String, Object> result,
        long latencyMs,
        String status
) {
}
