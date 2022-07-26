package com.appdynamics.extensions.memcached;

public class Constant {

    public static String USER_NAME;
    public static String PASSWORD;
    public static String ENCRYPTED_PASSWORD;
    public static String ENCRYPTION_KEY;
    public static String METRIC_SEPARATOR;
    public static String METRIC_PREFIX;
    public static String MONITOR_NAME;

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
