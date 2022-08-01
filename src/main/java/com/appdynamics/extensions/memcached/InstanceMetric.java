/*
 * Copyright 2018. AppDynamics LLC and its affiliates.
 * All Rights Reserved.
 * This is unpublished proprietary source code of AppDynamics LLC and its affiliates.
 * The copyright notice above does not evidence any actual or intended publication of such source code.
 */

package com.appdynamics.extensions.memcached;


//import com.appdynamics.extensions.util.metrics.Metric;
import com.appdynamics.extensions.logging.ExtensionsLoggerFactory;
import com.appdynamics.extensions.memcached.config.Configuration;
import com.appdynamics.extensions.memcached.config.Server;
import com.appdynamics.extensions.metrics.Metric;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.appdynamics.extensions.memcached.config.Server;

public class InstanceMetric {

    private String displayName;
    private Map<String,String> metricsMap;
    private List<Metric> allMetrics;
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(InstanceMetric.class);
    private final Server server;
    private final Configuration config;

    public InstanceMetric(String displayName, Map<String,String> metricsMap, Server server, Configuration config){
        this.displayName = displayName;
        this.metricsMap = metricsMap;
        this.server = server;
        this.config = config;
    }
    public List<Metric> populateMetrics(){
        //simple Metrics for now
        List<Metric> allMetrics = new ArrayList<Metric>();
        if(this.metricsMap != null) {
            for (Map.Entry<String, String> entry : this.metricsMap.entrySet()) {
                String metricKey = entry.getKey();
                String metricValue = entry.getValue();
                Metric metric = new Metric(metricKey, metricValue, this.config.getMetricPrefix() + Constant.METRIC_SEPARATOR + this.server.getDisplayName() + Constant.METRIC_SEPARATOR);
                if (metric != null) {
                    allMetrics.add(metric);
                } else {
                    logger.debug("Ignoring metric with metricKey= " + metricKey + " ,metricValue= " + metricValue);
                }
            }
        }
        this.setAllMetrics(allMetrics);
        return this.getAllMetrics();
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getMetricsMap() {
        if(metricsMap == null){
            metricsMap = new HashMap<String, String>();
        }
        return metricsMap;
    }

    public void setMetricsMap(Map<String, String> metricsMap) {
        this.metricsMap = metricsMap;
    }

    public List<Metric> getAllMetrics() {
        if(allMetrics == null){
            allMetrics = new ArrayList<Metric>();
        }
        return allMetrics;
    }

    public void setAllMetrics(List<Metric> allMetrics) {
        this.allMetrics = allMetrics;
    }
}
