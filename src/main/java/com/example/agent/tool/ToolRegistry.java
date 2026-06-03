package com.example.agent.tool;

import com.example.agent.data.MarketingDataService;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class ToolRegistry {

    private final Map<String, Function<Map<String, Object>, Map<String, Object>>> tools = new LinkedHashMap<>();

    public ToolRegistry(MarketingDataService marketingDataService) {
        tools.put("activity.lookup", args -> marketingDataService.lookupActivity((String) args.get("keyword")));
        tools.put("report.channelFunnel", args -> marketingDataService.channelFunnel((String) args.get("activityId")));
        tools.put("segment.query", args -> marketingDataService.segmentQuery((String) args.get("segmentName")));
        tools.put("coupon.policy", args -> marketingDataService.couponPolicy((String) args.get("couponName")));
    }

    public Map<String, Object> call(String toolName, Map<String, Object> arguments) {
        Function<Map<String, Object>, Map<String, Object>> tool = tools.get(toolName);
        if (tool == null) {
            throw new IllegalArgumentException("Unknown tool: " + toolName);
        }
        return tool.apply(arguments);
    }

    public List<ToolDescriptor> descriptors() {
        return List.of(
                new ToolDescriptor("activity.lookup", "根据关键词查询营销活动配置", Map.of("keyword", "string"), "LOW"),
                new ToolDescriptor("report.channelFunnel", "查询活动渠道漏斗和 ROI 指标", Map.of("activityId", "string"), "LOW"),
                new ToolDescriptor("segment.query", "查询用户人群包定义和规模", Map.of("segmentName", "string"), "MEDIUM"),
                new ToolDescriptor("coupon.policy", "查询优惠券策略和成本上限", Map.of("couponName", "string"), "MEDIUM")
        );
    }
}
