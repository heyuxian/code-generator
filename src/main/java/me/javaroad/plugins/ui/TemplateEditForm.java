package me.javaroad.plugins.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NamedConfigurable;
import java.awt.GridBagConstraints;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import lombok.Getter;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;
import me.javaroad.plugins.util.VelocityUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
public class TemplateEditForm extends NamedConfigurable<Template> {

    @Getter
    private JPanel mainPanel;
    private JPanel editPanel;
    private JTextField templateNameField;
    private JButton validateButton;
    private Editor editor;
    private Template template;
    private final TemplateGroup templateGroup;
    private String originName;

    public TemplateEditForm(TemplateGroup templateGroup, Template template) {
        this.templateGroup = templateGroup;
        this.template = template;
        //for validate template duplication
        this.originName = template.getName();
        templateNameField.setText(template.getName());
        initEditor(template.getContent());
        initValidation();
    }

    private void initValidation() {
        validateButton.addActionListener(event -> {
            try {
                VelocityUtils.evaluate(getTemplateContent(), null);
                Messages.showInfoMessage("Template validation succeeded", "Successful");
            } catch (Exception e) {
                Messages.showErrorDialog("Messages:\n"
                    + e.getMessage(), "Validation Failed");
            }
        });
    }

    @Override
    public boolean isModified() {
        return !(template.getName().equals(getTemplateName())
            && template.getContent().equals(getTemplateContent()));
    }

    @Override
    public void apply() throws ConfigurationException {
        // for validate template duplication
        templateGroup.removeTemplate(originName);
        if(templateGroup.exists(getTemplateName())) {
            throw new ConfigurationException("Duplicate template name: \'" + getTemplateName() + "\'");
        }
        template.setName(getTemplateName());
        template.setContent(getTemplateContent());
        templateGroup.addTemplate(template);
        originName = template.getName();
    }

    private String getTemplateContent() {
        return editor.getDocument().getText();
    }

    private String getTemplateName() {
        return templateNameField.getText();
    }

    @Override
    public void reset() {

    }

    @Override
    public void setDisplayName(String name) {
    }

    @Override
    public Template getEditableObject() {
        return template;
    }

    @Override
    public String getBannerSlogan() {
        return null;
    }

    @Override
    public JComponent createOptionsPanel() {
        return mainPanel;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return template.getName();
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    private void initEditor(String template) {
        EditorFactory factory = EditorFactory.getInstance();
        Document velocityTemplate = factory.createDocument(template);
        editor = factory.createEditor(velocityTemplate, null, FileTypeManager.getInstance()
            .getFileTypeByExtension("vm"), false);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        editPanel.add(editor.getComponent(), constraints);
    }

}
