package com.example.agent.tool;

import java.util.Map;

public record ToolDescriptor(
        String name,
        String description,
        Map<String, String> inputSchema,
        String riskLevel
) {
}
