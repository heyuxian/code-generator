package me.javaroad.plugins.util;

import com.google.common.collect.Maps;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.log4j.Log4j;
import me.javaroad.plugins.model.Entity;
import me.javaroad.plugins.model.Template;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;

/**
 * @author heyx
 */
@Log4j
public abstract class TemplateUtils {

    private static final String PACKAGE_NAME_REGEX = "package\\s*([a-zA-Z0-9_.]+);";
    private static final String CLASS_NAME_REGEX = "public\\s*(class|interface|abstract\\s*class)\\s([A-Z]+[a-zA-Z0-9_]*)\\s*";
    private static final Pattern CLASSNAME_PATTERN = Pattern.compile(CLASS_NAME_REGEX);
    private static final Pattern PACKAGE_NAME_PATTERN = Pattern.compile(PACKAGE_NAME_REGEX);

    public static void generate(Map<String, Template> templates, Entity entity,
        String basePackage, String outputPath) {
        Map<String, Object> context = buildContext(entity, basePackage);
        templates.forEach((name, template) -> {
            if (Objects.isNull(template) || StringUtils.isBlank(template.getContent())) {
                return;
            }
            String source = VelocityUtils.evaluate(template.getContent(), context);
            generateSourceFile(source, outputPath);
        });
    }

    private static boolean userConfirmedOverride(String name) {
        return Messages.showYesNoDialog(name + " already exists,\nConfirm Overwrite?", "File Exists", null)
            == Messages.OK;
    }

    private static void generateSourceFile(String source, String baseDir) {
        String packageName = extractPackage(source);
        String className = extractClassName(source);
        String sourcePath = buildSourcePath(baseDir, className, packageName);
        VirtualFileManager manager = VirtualFileManager.getInstance();
        VirtualFile virtualFile = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(sourcePath));
        if (virtualFile == null || !virtualFile.exists() || userConfirmedOverride(className)) {
            ApplicationManager.getApplication().runWriteAction(() -> {
                try {
                    if (virtualFile != null && virtualFile.exists()) {
                        virtualFile.setBinaryContent(source.getBytes("utf8"));

                    } else {
                        File file = new File(sourcePath);
                        FileUtils.writeStringToFile(file, source, "utf8");
                        manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(sourcePath));
                    }
                } catch (IOException e) {
                    log.error(e);
                }
            });
        }
    }

    private static String extractPackage(String source) {
        Matcher packageNameMatcher = PACKAGE_NAME_PATTERN.matcher(source);
        if (packageNameMatcher.find()) {
            return packageNameMatcher.group(1);
        }
        throw new RuntimeException("Template error, missing package");
    }

    private static String extractClassName(String source) {
        Matcher classNameMatcher = CLASSNAME_PATTERN.matcher(source);
        if (classNameMatcher.find()) {
            return classNameMatcher.group(2);
        }
        throw new RuntimeException("Template error, missing class");
    }


    private static String buildSourcePath(String baseDir, String className, String packageName) {
        return baseDir + "/" + packageName.replaceAll("\\.", "/")
            + "/" + className + ".java";
    }

    private static Map<String, Object> buildContext(Entity entity, String basePackage) {
        Map<String, Object> context = Maps.newHashMap();
        context.put("clazz", entity);
        context.put("fn", MyStringUtils.class);
        context.put("BASE_PACKAGE", basePackage);
        context.put("USER", System.getProperty("user.name"));

        Calendar calendar = Calendar.getInstance();
        context.put("YEAR", calendar.get(Calendar.YEAR));
        context.put("MONTH", calendar.get(Calendar.MONTH) + 1);
        context.put("DAY", calendar.get(Calendar.DAY_OF_MONTH));

        Date now = new Date();
        context.put("DATE", DateFormatUtils.format(now, "yyyy-MM-dd"));
        context.put("TIME", DateFormatUtils.format(now, "HH:mm:ss"));
        context.put("DATE_TIME", DateFormatUtils.format(now, "yyyy-MM-dd HH:mm:ss"));
        return context;
    }
}
