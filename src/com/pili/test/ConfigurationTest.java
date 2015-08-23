package com.pili.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.pili.Configuration;

public class ConfigurationTest {
    private static final String KEY_TEST_INTEGER = "test_integer";
    private static final String KEY_TEST_BOOLEAN = "test_boolean";
    private static final String KEY_TEST_STRING = "test_string";
    private static final String KEY_TEST_OBJECT = "test_object";

    @Test
    public void testConstruction() {
        assertEquals(Configuration.getInstance(), Configuration.getInstance());
    }

    @Test
    public void testSetAndGetInteger() {
        try {
            Configuration.getInstance().getInteger(KEY_TEST_INTEGER);
            fail();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }

        Configuration.getInstance().setInteger(KEY_TEST_INTEGER, 0);
        assertEquals(0, Configuration.getInstance().getInteger(KEY_TEST_INTEGER));
    }

    @Test
    public void testSetAndGetBoolean() {
        try {
            Configuration.getInstance().getBoolean(KEY_TEST_BOOLEAN);
            fail();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }

        Configuration.getInstance().setBoolean(KEY_TEST_BOOLEAN, true);
        assertEquals(true, Configuration.getInstance().getBoolean(KEY_TEST_BOOLEAN));
    }

    @Test
    public void testSetAndGetString() {
        try {
            Configuration.getInstance().getString(KEY_TEST_STRING);
            fail();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }

        assertEquals("test", Configuration.getInstance().getString(KEY_TEST_STRING, "test"));

        Configuration.getInstance().setString(KEY_TEST_STRING, "test1");
        assertEquals("test1", Configuration.getInstance().getString(KEY_TEST_STRING));
    }
    
    @Test
    public void testSetAndGetObject() {
        try {
            Configuration.getInstance().getString(KEY_TEST_OBJECT);
            fail();
        } catch (NullPointerException e) {
            // TODO: handle exception
        }
        
        // set string and get string
        Configuration.getInstance().setValue(KEY_TEST_OBJECT, "object00");
        assertEquals("object00", Configuration.getInstance().getString(KEY_TEST_OBJECT));
        
        // set boolean and get boolean
        Configuration.getInstance().setValue(KEY_TEST_OBJECT, true);
        assertEquals(true, Configuration.getInstance().getBoolean(KEY_TEST_OBJECT));
        
        // set int and get int
        Configuration.getInstance().setValue(KEY_TEST_OBJECT, 1);
        assertEquals(1, Configuration.getInstance().getInteger(KEY_TEST_OBJECT));
    }
}
