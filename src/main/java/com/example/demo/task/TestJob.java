package com.example.demo.task;

import javax.annotation.PostConstruct;

import com.example.demo.config.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

@Component
public class TestJob extends AbstractJob {

    private static final Logger log = LoggerFactory.getLogger("testJob");

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public TestJob(
            @Value("${test.job.cron}") String cron,
            @Value("${test.job.shardingTotalCount}") Integer shardingTotalCount,
            @Value("${test.job.shardingItemParameters}") String shardingItemParameters,
            @Value("${test.job.jobParameter}") String jobParameter) {
        super(cron, shardingTotalCount, shardingItemParameters, jobParameter);
    }

    @PostConstruct
    public void init() {
        start(zookeeperRegistryCenter);
    }

    @Override
    public void executeJob(Integer shardingTotalCount, Integer shardingItem,
            String itemParameter, String jobParameter) {
        log.info("这是一个测试任务");
    }
}