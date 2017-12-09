package me.javaroad.plugins.util;

import com.google.common.collect.Maps;
import java.io.File;
import java.util.Map;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author heyx
 */
public class ZipFileUtilsTest {

    @Test
    public void writeAndReadTemplate() throws Exception {
        Map<String, TemplateGroup> templateGroupMap = buildTemplateGroup();
        String tmpDir = System.getProperty("java.io.tmpdir");
        String path = ZipFileUtils.writeTemplateToFile(templateGroupMap, tmpDir);
        Assert.assertNotNull(path);
        File zipFile = new File(path);
        Assert.assertTrue(zipFile.exists());

        //read template from path
        Map<String, TemplateGroup> result = ZipFileUtils.readTemplateFromFile(path);
        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 2);
        Assert.assertEquals(result.get("group1").getTemplateMap().size(), 2);
        Assert.assertEquals(result.get("group2").getTemplateMap().size(), 1);
    }

    private Map<String,TemplateGroup> buildTemplateGroup() {
        TemplateGroup templateGroup1 = new TemplateGroup("group1");
        TemplateGroup templateGroup2 = new TemplateGroup("group2");
        Template template1 = new Template("template1", "content");
        Template template2 = new Template("template2", "content");
        Template template3 = new Template("template3", "content");
        templateGroup1.addTemplate(template1);
        templateGroup1.addTemplate(template2);
        templateGroup2.addTemplate(template3);
        Map<String,TemplateGroup> templateGroupMap = Maps.newHashMap();
        templateGroupMap.put(templateGroup1.getName(), templateGroup1);
        templateGroupMap.put(templateGroup2.getName(), templateGroup2);
        return templateGroupMap;
    }


}