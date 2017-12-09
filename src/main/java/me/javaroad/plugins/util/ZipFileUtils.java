package me.javaroad.plugins.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;

/**
 * @author heyx
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ZipFileUtils {

    public static String writeTemplateToFile(Map<String, TemplateGroup> templateGroupMap, String path) {
        Gson gson = new Gson();
        File zipFile = new File(getDefaultFileName(path));
        if (!zipFile.exists()) {
            try {
                zipFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(zipFile))) {
            templateGroupMap.forEach((groupName, group) -> {
                group.getTemplateMap().forEach((templateName, template) -> {
                    ZipEntry zipEntry = new ZipEntry(groupName + "/" + templateName + ".json");
                    try {
                        outputStream.putNextEntry(zipEntry);
                        outputStream.write(gson.toJson(template).getBytes("utf8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return zipFile.getPath();
    }

    public static Map<String, TemplateGroup> readTemplateFromFile(String path) {
        Gson gson = new Gson();
        Map<String, TemplateGroup> templateGroupMap = Maps.newHashMap();
        try {
            ZipFile zipFile = new ZipFile(path);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            // todo multi group import
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.isDirectory()) {
                    continue;
                }
                if (!zipEntry.getName().matches(".+/[^/]+\\.json")) {
                    throw new IllegalArgumentException("invalid template file");
                }
                String[] name = zipEntry.getName().split("/");
                TemplateGroup templateGroup;
                if (templateGroupMap.containsKey(name[0])) {
                    templateGroup = templateGroupMap.get(name[0]);
                } else {
                    templateGroup = new TemplateGroup(name[0]);
                }
                Template template = gson
                    .fromJson(new InputStreamReader(zipFile.getInputStream(zipEntry)), Template.class);
                templateGroup.addTemplate(template);

                templateGroupMap.put(templateGroup.getName(), templateGroup);
            }
            return templateGroupMap;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String getDefaultFileName(String path) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        String defaultName = "template-" + dateFormat.format(new Date()) + ".zip";
        return path + defaultName;
    }

}
