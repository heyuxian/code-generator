package me.javaroad.plugins.ui;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.PackageChooser;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiPackage;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import lombok.Getter;

/**
 * @author heyx
 */
public class SelectPathForm {

    @Getter
    private JPanel mainPanel;
    private Project project;

    private JTextField basePackageText;
    private JButton basePackageBtn;
    private JTextField outPutPathText;
    private JButton outputPathBtn;

    public SelectPathForm(Project project) {
        this.project = project;
        initBtn();
    }

    private void initBtn() {
        basePackageBtn.addActionListener(e -> {
            PackageChooser packageChooser = new PackageChooserDialog("Select Base Package", project);
            packageChooser.show();
            PsiPackage psiPackage = packageChooser.getSelectedPackage();
            if (Objects.nonNull(psiPackage)) {
                basePackageText.setText(psiPackage.getQualifiedName());
            }
        });

        outputPathBtn.addActionListener(e -> {
            FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
            VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
            if (Objects.nonNull(virtualFile)) {
                outPutPathText.setText(virtualFile.getPath());
            }
        });
    }

    public String getOutputPath() {
        return outPutPathText.getText();
    }

    public String getBasePackage() {
        return basePackageText.getText();
    }
}
