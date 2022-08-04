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
import com.appdynamics.extensions.util.ValidationUtils;
import org.slf4j.Logger;
import com.google.common.cache.Cache;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceMetric {

    private String displayName;
    private Map<String,String> metricsMap;
    private List<Metric> allMetrics;
    public static final Logger logger = ExtensionsLoggerFactory.getLogger(InstanceMetric.class);
    private final Server server;
    private final Configuration config;
    private final Cache<String, BigDecimal> cache;

    public InstanceMetric(String displayName, Map<String,String> metricsMap, Server server, Configuration config, Cache<String, BigDecimal> cache){
        this.displayName = displayName;
        this.metricsMap = metricsMap;
        this.server = server;
        this.config = config;
        this.cache = cache;
    }
    public void populateMetrics(){

        List<Metric> allMetrics = new ArrayList<>();
        if(this.metricsMap != null) {
            for (Map.Entry<String, String> entry : this.metricsMap.entrySet()) {
                String metricKey = entry.getKey();
                String metricValue = entry.getValue();
                try {
                    //validation utils throws warning and stack anytime not valid num
                    if (ValidationUtils.isValidMetricValue(metricValue)) {

                        BigDecimal val = new BigDecimal(metricValue);
                        String metricPath = this.config.getMetricPrefix() + this.server.getDisplayName() + Constant.METRIC_SEPARATOR;
                        String full_metric_path = metricPath + metricKey;
                        if (this.config.getIgnoreDelta().contains(metricKey)) {
                            logger.debug("Ignore delta calculation for {}" + full_metric_path);
                        } else {
                            //TODO as it turns out DeltaMetricsCalculator will do this
                            //could replace the global cache pointer with this class instance
                            //just run metricValue = calculateDelta(full_metric_path, val)
                            BigDecimal cache_val = this.cache.getIfPresent(full_metric_path);
                            if (cache_val != null) {
                                cache.put(full_metric_path, val);
                                BigDecimal deltaValue = val.subtract(cache_val);
                                metricValue = deltaValue.toString();
                            }

                        }
                        Metric metric = new Metric(metricKey, metricValue, full_metric_path);

                        allMetrics.add(metric);
                    }
                }
                catch (Exception e){
                    logger.debug("Ignoring metric with metricKey= " + metricKey + " ,metricValue= " + metricValue);
                    logger.error(e.getMessage());
                }

            }
        }
        this.setAllMetrics(allMetrics);
    }
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Map<String, String> getMetricsMap() {
        if(metricsMap == null){
            metricsMap = new HashMap<>();
        }
        return metricsMap;
    }

    public void setMetricsMap(Map<String, String> metricsMap) {
        this.metricsMap = metricsMap;
    }

    public List<Metric> getAllMetrics() {
        if(allMetrics == null){
            allMetrics = new ArrayList<>();
        }
        return allMetrics;
    }

    public void setAllMetrics(List<Metric> allMetrics) {
        this.allMetrics = allMetrics;
    }
}
