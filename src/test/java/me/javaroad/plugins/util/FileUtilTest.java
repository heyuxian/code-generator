package me.javaroad.plugins.util;

import com.intellij.openapi.util.io.FileUtil;
import java.io.IOException;
import me.javaroad.plugins.settings.TemplateSettings;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author heyx
 */
public class FileUtilTest {

    @Test
    public void loadText() throws IOException {
        String content = FileUtil.loadTextAndClose(TemplateSettings.class.getResourceAsStream("/templates/Mapper.vm"));
        Assert.assertEquals(true, StringUtils.isNotBlank(content));
    }
}
