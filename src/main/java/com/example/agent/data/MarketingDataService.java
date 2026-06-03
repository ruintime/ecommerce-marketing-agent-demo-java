package com.example.agent.data;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MarketingDataService {

    public Map<String, Object> lookupActivity(String keyword) {
        Map<String, Object> activity = new LinkedHashMap<>();
        if (keyword.contains("秒杀")) {
            activity.put("activityId", "cmp-618-seckill");
            activity.put("name", "618 秒杀活动");
            activity.put("status", "RUNNING");
            activity.put("budget", 280000);
            activity.put("owner", "增长运营组");
            return activity;
        }
        activity.put("activityId", "cmp-new-user-gift");
        activity.put("name", "新人礼包活动");
        activity.put("status", "RUNNING");
        activity.put("budget", 120000);
        activity.put("owner", "用户增长组");
        return activity;
    }

    public Map<String, Object> channelFunnel(String activityId) {
        Map<String, Object> report = new LinkedHashMap<>();
        report.put("activityId", activityId);
        report.put("exposure", 830000);
        report.put("clickRate", "3.8%");
        report.put("registerRate", "18.4%");
        report.put("payRate", "2.1%");
        report.put("roi", "0.82");
        report.put("cpa", "96.5");
        report.put("cac", "118.0");
        report.put("weakPoint", "抖音渠道点击后支付转化偏低，B站渠道注册成本偏高");
        return report;
    }

    public Map<String, Object> segmentQuery(String segmentName) {
        Map<String, Object> segment = new LinkedHashMap<>();
        segment.put("segmentId", "seg-high-value-churn");
        segment.put("segmentName", segmentName);
        segment.put("userCount", 1260);
        segment.put("criteria", "近 90 天消费金额 Top 20%，且近 30 天未支付");
        segment.put("risk", "需要控制触达频率，避免重复补贴");
        return segment;
    }

    public Map<String, Object> couponPolicy(String couponName) {
        Map<String, Object> coupon = new LinkedHashMap<>();
        coupon.put("couponId", "coupon-churn-20");
        coupon.put("couponName", couponName);
        coupon.put("threshold", "满 99 减 20");
        coupon.put("validDays", 7);
        coupon.put("costUpperBound", 25200);
        return coupon;
    }
}
