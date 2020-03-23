package com.example.demo.task;

import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.example.demo.config.AbstractJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class FirstJob extends AbstractJob {

    private static final Logger log = LoggerFactory.getLogger("firstJob");

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    public FirstJob(
            @Value("${first.job.cron}") String cron,
            @Value("${first.job.shardingTotalCount}") Integer shardingTotalCount,
            @Value("${first.job.shardingItemParameters}") String shardingItemParameters,
            @Value("${first.job.jobParameter}") String jobParameter) {
        super(cron, shardingTotalCount, shardingItemParameters, jobParameter);
    }

    @PostConstruct
    public void init() {
        start(zookeeperRegistryCenter);
    }


    @Override
    public void executeJob(Integer shardingTotalCount, Integer shardingItem, String itemParameter, String jobParameter) {
        log.info("这是第一个简单的任务");
    }
}