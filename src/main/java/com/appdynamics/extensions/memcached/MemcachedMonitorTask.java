package com.appdynamics.extensions.memcached;

import com.appdynamics.extensions.AMonitorTaskRunnable;
import com.appdynamics.extensions.MetricWriteHelper;
import com.appdynamics.extensions.conf.MonitorContextConfiguration;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.memcached.config.Server;
import static com.appdynamics.extensions.memcached.Constant.*;
import com.appdynamics.extensions.memcached.config.Configuration;
import com.appdynamics.extensions.metrics.Metric;
import com.google.common.collect.Lists;
import com.singularity.ee.agent.systemagent.api.MetricWriter;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.command.BinaryCommandFactory;
import net.rubyeye.xmemcached.utils.AddrUtil;
import org.slf4j.Logger;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;

public class MemcachedMonitorTask implements AMonitorTaskRunnable {
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(MemcachedMonitorTask.class);
    private final Server server;
    private final MetricWriteHelper metricWriteHelper;
    private final MonitorContextConfiguration contextConfiguration;
    private final String metricPathPrefix;
    private Configuration config;
    public MemcachedMonitorTask(MonitorContextConfiguration contextConfiguration, MetricWriteHelper metricWriteHelper, Server server, Configuration config) {
        this.server = server;
        this.contextConfiguration = contextConfiguration;
        this.metricWriteHelper = metricWriteHelper;
        this.metricPathPrefix = contextConfiguration.getMetricPrefix() + METRIC_SEPARATOR + server.getDisplayName() + METRIC_SEPARATOR;
        this.config = config;
    }

    @Override
    public void onTaskComplete() {

    }

    @Override
    public void run() {

    }

    private void printMetrics(List<Metric> allMetrics, String displayName) {
        Set<String> ignoreDelta = config.getIgnoreDelta();
        String prefix = getMetricPrefix();
        List<Metric> metrics_list = Lists.newArrayList();
        for(Metric aMetric:allMetrics) {
            String metricPath = prefix + aMetric.getMetricPath();
            BigInteger metricValue = new BigInteger(aMetric.getMetricValue());
            //Metric m = new Metric();
            if (ignoreDelta.contains(aMetric.getMetricPath())) {
                logger.debug("Ignore delta calculation for {}" + metricPath);
                printMetric(metricPath, metricValue.toString(), aMetric.get.getAggregator(), aMetric.getTimeRollup(), aMetric.getClusterRollup());
            }
            else{
                BigInteger prevValue = cache.getIfPresent(metricPath);
                cache.put(metricPath, metricValue);
                if(prevValue != null){
                    BigInteger deltaValue = metricValue.subtract(prevValue);
                    printMetric(metricPath, deltaValue.toString(), aMetric.getAggregator(), aMetric.getTimeRollup(), aMetric.getClusterRollup());
                }

            }
        }
    }

    /**
     * Builds a memcached client.
     * @return MemcachedClient
     * @throws IOException
     */
    private MemcachedClient getMemcachedClient() throws IOException {
        String[] host_port = this.server.getServer().split(":");
        XMemcachedClient client = new XMemcachedClient(host_port[0],Integer.parseInt(host_port[1]));
        return client;
    }

    /*private void printMetric(String metricName,String metricValue,String aggType,String timeRollupType,String clusterRollupType){
        MetricWriter metricWriter = this.metricWriteHelper(metricName,
                aggType,
                timeRollupType,
                clusterRollupType
        );
        //   System.out.println("Sending [" + aggType + METRIC_SEPARATOR + timeRollupType + METRIC_SEPARATOR + clusterRollupType
        //           + "] metric = " + metricName + " = " + metricValue);
        logger.debug("Sending [{}|{}|{}] metric= {},value={}", aggType, timeRollupType, clusterRollupType,metricName,metricValue);
        metricWriter.printMetric(metricValue);
    }*/
    private String getMetricPrefix() {
        return config.getMetricPrefix() + this.server.getDisplayName() + METRIC_SEPARATOR;
    }
}
