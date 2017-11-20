package me.javaroad.plugins.config;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import javax.swing.JComponent;
import me.javaroad.plugins.settings.TemplateSettings;
import me.javaroad.plugins.ui.HelpPanel;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
public class CodeGeneratorConfigurable extends SearchableConfigurable.Parent.Abstract implements Configurable.NoScroll {

    private TemplateSettings templateSettings;
    private final Project project;
    private final HelpPanel helpPanel;

    public CodeGeneratorConfigurable(Project project) {
        this.project = project;
        this.helpPanel = new HelpPanel();
        templateSettings = ServiceManager.getService(TemplateSettings.class);
    }

    @NotNull
    @Override
    public String getId() {
        return "generator.configurable";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Code Generator";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return getId();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return helpPanel.getMainPanel();
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public void apply() throws ConfigurationException {
    }

    @Override
    public void reset() {
    }

    @Override
    protected Configurable[] buildConfigurables() {
        Configurable[] configurables = new Configurable[1];
        configurables[0] = new TemplateEditorConfigurable(project, templateSettings.getState());
        return configurables;
    }
}
