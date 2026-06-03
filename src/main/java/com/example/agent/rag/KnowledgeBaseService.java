package com.example.agent.rag;

import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class KnowledgeBaseService {

    private final List<KnowledgeChunk> chunks = List.of(
            new KnowledgeChunk(
                    "kb-001",
                    "营销活动归因口径",
                    "渠道 ROI = 支付 GMV / 渠道消耗。CPA 关注每个关键转化成本，CAC 关注新增付费用户成本。多次访问时采用最近一次有效点击归因，归因窗口为 7 天。",
                    List.of("ROI", "CPA", "CAC", "归因", "渠道")
            ),
            new KnowledgeChunk(
                    "kb-002",
                    "发券操作风控规则",
                    "批量发券、预算调整、活动上下线属于高风险写操作。Agent 只能生成待审批动作，必须经过运营负责人确认后才可以执行。",
                    List.of("发券", "优惠券", "审批", "风控", "预算")
            ),
            new KnowledgeChunk(
                    "kb-003",
                    "新人礼包活动 SOP",
                    "新人礼包活动需要检查渠道来源、落地页加载、券门槛、注册转化、首单支付和券核销。如果点击率正常但支付率下降，优先排查权益吸引力和订单优惠试算。",
                    List.of("新人", "礼包", "支付", "核销", "落地页")
            ),
            new KnowledgeChunk(
                    "kb-004",
                    "秒杀活动稳定性要求",
                    "秒杀活动需要使用 Redis 预扣库存、Lua 原子扣减、用户参与幂等 Key、MQ 异步落库和补偿任务，避免超发和重复参与。",
                    List.of("秒杀", "Redis", "Lua", "库存", "幂等")
            )
    );

    public List<KnowledgeChunk> search(String query, int limit) {
        return chunks.stream()
                .sorted(Comparator.comparingInt(chunk -> -score(query, chunk)))
                .limit(limit)
                .toList();
    }

    private int score(String query, KnowledgeChunk chunk) {
        int score = 0;
        for (String tag : chunk.tags()) {
            if (query.contains(tag)) {
                score += 3;
            }
        }
        for (String token : query.split("\\s+")) {
            if (!token.isBlank() && chunk.content().contains(token)) {
                score++;
            }
        }
        return score;
    }
}
