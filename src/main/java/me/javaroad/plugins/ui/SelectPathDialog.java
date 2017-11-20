package me.javaroad.plugins.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.PackageChooser;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;
import java.util.Map;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.javaroad.plugins.model.TemplateGroup;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
public class SelectPathDialog extends DialogWrapper {

    private JButton outputPathBtn;
    private JButton basePackageBtn;
    private JTextField outputPathField;
    private JTextField basePackageField;
    private JComboBox<String> templateGroupCombo;
    private JPanel mainPanel;
    private final Project project;
    private final Map<String, TemplateGroup> templateGroupMap;

    public SelectPathDialog(Project project, Map<String, TemplateGroup> templateGroupMap) {
        super(project);
        this.project = project;
        this.templateGroupMap = templateGroupMap;
        setTitle("Select Path");
        initBtn();
        initCombo();
        init();
    }

    @Override
    protected void doOKAction() {
        if (StringUtils.isBlank(getOutputPath())) {
            outputPathField.requestFocus();
            return;
        }
        if (StringUtils.isBlank(getBasePackage())) {
            basePackageField.requestFocus();
            return;
        }
        if (StringUtils.isBlank(getTemplateGroup())) {
            templateGroupCombo.requestFocus();
            return;
        }
        close(0);
    }

    public String getOutputPath() {
        return outputPathField.getText();
    }

    public String getBasePackage() {
        return basePackageField.getText();
    }

    public String getTemplateGroup() {
        Object selectedGroup = templateGroupCombo.getSelectedItem();
        if (Objects.nonNull(selectedGroup)) {
            return String.valueOf(selectedGroup);
        }
        return null;
    }

    private void initCombo() {
        templateGroupMap.forEach((key, value) -> {
            templateGroupCombo.addItem(key);
        });
    }

    private void initBtn() {
        basePackageBtn.addActionListener(e -> {
            PackageChooser packageChooser = new PackageChooserDialog("Select Base Package", project);
            packageChooser.show();
            PsiPackage psiPackage = packageChooser.getSelectedPackage();
            if (Objects.nonNull(psiPackage)) {
                basePackageField.setText(psiPackage.getQualifiedName());
            }
        });

        outputPathBtn.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (Objects.nonNull(virtualFile)) {
                outputPathField.setText(virtualFile.getPath());
            }
        });
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }
}
