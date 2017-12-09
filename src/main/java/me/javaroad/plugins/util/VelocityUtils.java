package me.javaroad.plugins.util;

import java.io.StringWriter;
import java.util.Map;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.log.NullLogChute;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class VelocityUtils {

    private static final VelocityEngine ENGINE = new VelocityEngine();

    static {
        ENGINE.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
            NullLogChute.class.getName());
        ENGINE.init();
    }

    public static String evaluate(String template, Map<String, Object> map) {
        VelocityContext context = new VelocityContext();
        if (Objects.nonNull(map)) {
            map.forEach(context::put);
        }
        StringWriter writer = new StringWriter();
        ENGINE.evaluate(context, writer, "", template);
        return writer.toString();
    }

}
