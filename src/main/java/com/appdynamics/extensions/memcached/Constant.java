package com.appdynamics.extensions.memcached;

public class Constant {

    /*public static String USER_NAME;
    public static String PASSWORD;
    public static String ENCRYPTED_PASSWORD;
    public static String ENCRYPTION_KEY;*/
    public static String METRIC_SEPARATOR;
    public static String METRIC_PREFIX;
    public static String MONITOR_NAME;
    public static final String CFG_DISPLAY_NAME = "displayName";
    public static final String CFG_SERVERS = "servers";
    public static final String CFG_SERVER = "server";
    public static final String CFG_METRIC_PREFIX = "metricPrefix";
    public static final String CFG_TIMEOUT = "timeout";
    public static final String CFG_IGNORE_DELTA = "ignoreDelta";
    public static final String CFG_IGNORE_METRIC = "ignoreMetric";

    static {
        METRIC_PREFIX = "Custom Metrics|Memcached";
        MONITOR_NAME = "Memcached Monitor";
        METRIC_SEPARATOR = "|";
        /*
        USER_NAME = "username";
        ENCRYPTED_PASSWORD = "encryptedPassword";
        ENCRYPTION_KEY = "encryptionKey";
        PASSWORD = "password";
         */
    }


}
