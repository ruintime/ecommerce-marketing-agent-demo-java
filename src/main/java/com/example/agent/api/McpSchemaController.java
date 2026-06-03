package com.example.agent.api;

import com.example.agent.tool.ToolDescriptor;
import com.example.agent.tool.ToolRegistry;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/mcp")
public class McpSchemaController {

    private final ToolRegistry toolRegistry;

    public McpSchemaController(ToolRegistry toolRegistry) {
        this.toolRegistry = toolRegistry;
    }

    @GetMapping("/tools")
    public List<ToolDescriptor> tools() {
        return toolRegistry.descriptors();
    }
}
