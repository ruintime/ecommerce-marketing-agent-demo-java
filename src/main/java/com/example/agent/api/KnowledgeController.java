package com.example.agent.api;

import com.example.agent.rag.KnowledgeBaseService;
import com.example.agent.rag.KnowledgeChunk;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/knowledge")
public class KnowledgeController {

    private final KnowledgeBaseService knowledgeBaseService;

    public KnowledgeController(KnowledgeBaseService knowledgeBaseService) {
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @GetMapping("/search")
    public List<KnowledgeChunk> search(@RequestParam String q) {
        return knowledgeBaseService.search(q, 5);
    }
}
