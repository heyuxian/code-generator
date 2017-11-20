package me.javaroad.plugins.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author heyx
 */
public class MyStringUtilsTest {

    @Test
    public void pluralize() throws Exception {
        Assert.assertEquals("Tests", MyStringUtils.pluralize("Test"));
    }

    @Test
    public void singularize() throws Exception {
        Assert.assertEquals("Test", MyStringUtils.singularize("Tests"));
    }

    @Test
    public void decapitalize() throws Exception {
        Assert.assertEquals("test", MyStringUtils.decapitalize("Test"));
    }

    @Test
    public void capitalize() throws Exception {
        System.out.println(MyStringUtils.capitalize("Test"));
        Assert.assertEquals("Test", MyStringUtils.capitalize("test"));
    }

    @Test
    public void dcp() throws Exception {
        Assert.assertEquals("tests", MyStringUtils.dcp("Test"));
    }

}