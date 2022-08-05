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
import com.appdynamics.extensions.metrics.DeltaMetricsCalculator;
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
    private final DeltaMetricsCalculator deltaMetricsCalculator;

    public InstanceMetric(String displayName, Map<String,String> metricsMap, Server server, Configuration config, DeltaMetricsCalculator deltaMetricsCalculator){
        this.displayName = displayName;
        this.metricsMap = metricsMap;
        this.server = server;
        this.config = config;
        this.deltaMetricsCalculator = deltaMetricsCalculator;
    }
    public void populateMetrics(){

        List<Metric> allMetrics = new ArrayList<>();
        if(this.metricsMap != null) {
            for (Map.Entry<String, String> entry : this.metricsMap.entrySet()) {
                String metricKey = entry.getKey();
                String metricValue = entry.getValue();
                //try {
                    //validation utils throws warning and stack anytime not valid num
                    //let try and head that off with our own check
                    //Double.parseDouble(metricValue);
                    //if it turns out logging outside workbench is too noisy
                    if (ValidationUtils.isValidMetricValue(metricValue)) {

                        BigDecimal val = new BigDecimal(metricValue);
                        String metricPath = this.config.getMetricPrefix() + this.server.getDisplayName() + Constant.METRIC_SEPARATOR;
                        String full_metric_path = metricPath + metricKey;
                        if (this.config.getIgnoreDelta().contains(metricKey)) {
                            logger.debug("Ignore delta calculation for {}" + full_metric_path);
                        } else {
                            val = this.deltaMetricsCalculator.calculateDelta(full_metric_path, val);
                            //returns null if not in the cache, so that in theory means we are getting
                            //first metric so just use it
                            if(val != null) {
                                metricValue = val.toString();
                            }
                        }
                        Metric metric = new Metric(metricKey, metricValue, full_metric_path);

                        allMetrics.add(metric);
                    }
                    else {
                        logger.debug("Ignoring metric with metricKey= " + metricKey + " ,metricValue= " + metricValue);
                    }
                /*}
                catch (Exception e){
                    logger.debug("Ignoring metric with metricKey= " + metricKey + " ,metricValue= " + metricValue);
                    logger.error(e.toString());
                }*/

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
