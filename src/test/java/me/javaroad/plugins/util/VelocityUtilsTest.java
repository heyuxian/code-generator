package me.javaroad.plugins.util;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

/**
 * @author heyx
 */
public class VelocityUtilsTest {

    @Test
    public void evaluate() throws Exception {
        Map<String, Object> map = new HashMap<>();
        map.put("clazz", "clazz");
        VelocityUtils.evaluate("${clazz}", map);
    }

}
