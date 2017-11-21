package me.javaroad.plugins.util;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author heyx
 */
public class MyStringUtilsTest {

    @Test
    public void pluralize() throws Exception {
        Assert.assertEquals("Categories", MyStringUtils.pluralize("Category"));
    }

    @Test
    public void singularize() throws Exception {
        Assert.assertEquals("Category", MyStringUtils.singularize("Categories"));
    }

    @Test
    public void decapitalize() throws Exception {
        Assert.assertEquals("category", MyStringUtils.decapitalize("Category"));
    }

    @Test
    public void capitalize() throws Exception {
        Assert.assertEquals("Category", MyStringUtils.capitalize("category"));
    }

    @Test
    public void dcp() throws Exception {
        Assert.assertEquals("categories", MyStringUtils.dcp("Category"));
    }

}