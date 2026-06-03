package com.example.agent.rag;

import java.util.List;

public record KnowledgeChunk(
        String id,
        String title,
        String content,
        List<String> tags
) {
}
