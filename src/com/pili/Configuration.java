package com.pili;

import java.util.HashMap;
import java.util.Map;

public class Configuration {
    private Configuration() {}

    private static class ConfigurationHolder {
        public final static Configuration instance = new Configuration();
    }

    public static Configuration getInstance() {
        return ConfigurationHolder.instance;
    }

    private Map<String, Object> mMap = new HashMap<String, Object>();

    public static final String KEY_API_HOST = "api_host";
    public static final String KEY_API_VERSION = "api_version";
    public static final String KEY_USE_HTTPS = "use_https";

    public final boolean containsKey(String name) {
        return mMap.containsKey(name);
    }

    Map<String, Object> getMap() {
        return mMap;
    }

    /**
     * Returns the value of an integer key.
     */
    public final int getInteger(String name) {
        return ((Integer)mMap.get(name)).intValue();
    }

    /**
     * Returns the value of an Boolean key.
     */
    public final boolean getBoolean(String name) {
        return ((Boolean)mMap.get(name)).booleanValue();
    }

    /**
     * Returns the value of a string key.
     */
    public final String getString(String name) {
        return mMap.get(name).toString();
    }

    /**
     * Returns the value of an String key, or the default value if the
     * key is missing or is for another type value.
     */
    public final String getString(String name, String defaultValue) {
        try {
            return getString(name);
        }
        catch (NullPointerException  e) { /* no such field */ }
        catch (ClassCastException e) { /* field of different type */ }
        return defaultValue;
    }

    /**
     * Sets the value of an integer key.
     */
    public final void setInteger(String name, int value) {
        mMap.put(name, Integer.valueOf(value));
    }

    /**
     * Sets the value of an Boolean key.
     */
    public final void setBoolean(String name, Boolean value) {
        mMap.put(name, Boolean.valueOf(value));
    }

    /**
     * Sets the value of a string key.
     */
    public final void setString(String name, String value) {
        mMap.put(name, value);
    }

    /**
     * Sets the value of a Object key.
     */
    public final void setValue(String name, Object value) {
        mMap.put(name, value);
    }
}
