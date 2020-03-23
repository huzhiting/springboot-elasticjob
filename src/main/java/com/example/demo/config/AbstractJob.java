package com.example.demo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;

/**
 * 抽象的任务
 */
public abstract class AbstractJob implements SimpleJob {

    protected static final Logger LOG = LoggerFactory.getLogger("job");

    /** 任务执行时间表达式 */
    private String cron;
    /** 任务分片总数 */
    private Integer shardingTotalCount;
    /** 任务分片参数 */
    private String shardingItemParameters;
    /** 任务参数 */
    private String jobParameter;
    /** 是否已经启动 */
    private boolean started = false;

    public AbstractJob() {
        super();
    }

    /**
     * 构造函数
     * 
     * @param cron
     * @param shardingTotalCount
     * @param shardingItemParameters
     * @param jobParameter
     */
    public AbstractJob(String cron, Integer shardingTotalCount,
            String shardingItemParameters, String jobParameter) {
        super();
        this.cron = cron;
        this.shardingTotalCount = shardingTotalCount;
        this.shardingItemParameters = shardingItemParameters;
        this.jobParameter = jobParameter;
    }

    /**
     * 启动任务,调用此方法，将启动任务调度
     * @param zookeeperRegistryCenter 任务注册中心
     */
    protected synchronized void start(ZookeeperRegistryCenter zookeeperRegistryCenter) {
        // 保证一个任务只执行一次start
        if (this.started){
            return;
        }
        this.started = true;
        SpringJobScheduler s = new SpringJobScheduler(this,
                zookeeperRegistryCenter, getLiteJobConfiguration());
        s.init();
    }

    @Override
    public void execute(ShardingContext shardingContext) {
        LOG.info(String.format("Thread ID:%s,任务总片数:%s," + "当前分片项:%s,当前参数:%s,"
                + "当前任务名称:%s,当前任务参数:%s", Thread.currentThread().getId(),
                shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobName(), shardingContext.getJobParameter()));
        executeJob(shardingContext.getShardingTotalCount(),
                shardingContext.getShardingItem(),
                shardingContext.getShardingParameter(),
                shardingContext.getJobParameter());
    }

    /**
     * 执行任务
     * 
     * @param shardingTotalCount
     *            任务分片数
     * @param shardingItem
     *            当前分配序号
     * @param jobParameter
     *            当前分配任务参数
     */
    public abstract void executeJob(Integer shardingTotalCount,
            Integer shardingItem, String itemParameter, String jobParameter);

    /**
     * 获取任务配置
     * @return
     */
    protected LiteJobConfiguration getLiteJobConfiguration() {
        return LiteJobConfiguration
                .newBuilder(
                        new SimpleJobConfiguration(JobCoreConfiguration
                                .newBuilder(this.getClass().getName(),
                                        this.cron, this.shardingTotalCount)
                                .shardingItemParameters(
                                        this.shardingItemParameters)
                                .jobParameter(this.jobParameter).build(), this
                                .getClass().getCanonicalName()))
                .overwrite(true).build();
    }

}