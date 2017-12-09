package me.javaroad.plugins.settings;

import com.google.common.collect.Maps;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import java.io.IOException;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
@State(name = "CodeGeneratorSettings", storages = {@Storage("$APP_CONFIG$/code-generator-settings.xml")})
public class TemplateSettings implements PersistentStateComponent<TemplateSettings> {

    @Getter
    private final Map<String, TemplateGroup> templateGroupMap = Maps.newHashMap();
    @Getter
    @Setter
    private Boolean init = Boolean.FALSE;

    @Nullable
    @Override
    public TemplateSettings getState() {
        if (templateGroupMap.size() == 0 && !init) {
            loadDefaultState();
            init = true;
        }
        return this;
    }

    private void loadDefaultState() {
        templateGroupMap.clear();
        try {
            TemplateGroup defaultGroup = new TemplateGroup();
            defaultGroup.setName("Default");
            defaultGroup.getTemplateMap().put("Controller", loadTemplate("Controller"));
            defaultGroup.getTemplateMap().put("Mapper", loadTemplate("Mapper"));
            defaultGroup.getTemplateMap().put("Repository", loadTemplate("Repository"));
            defaultGroup.getTemplateMap().put("Service", loadTemplate("Service"));
            defaultGroup.getTemplateMap().put("Request", loadTemplate("Request"));
            defaultGroup.getTemplateMap().put("Response", loadTemplate("Response"));
            templateGroupMap.put(defaultGroup.getName(), defaultGroup);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private Template loadTemplate(String templateName) throws IOException {
        String templateContent = FileUtil
            .loadTextAndClose(TemplateSettings.class.getResourceAsStream("/templates/" + templateName + ".vm"));
        return new Template(templateName, templateContent);
    }

    @Override
    public void loadState(TemplateSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public TemplateGroup getTemplateGroup(String groupName) {
        return templateGroupMap.get(groupName);
    }

    public void save(Map<String, TemplateGroup> newTemplateGroupMap) {
        templateGroupMap.clear();
        newTemplateGroupMap.forEach(templateGroupMap::put);
    }

}
