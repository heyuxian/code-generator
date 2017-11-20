package me.javaroad.plugins.action;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import me.javaroad.plugins.model.Entity;
import me.javaroad.plugins.model.Field;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.settings.TemplateSettings;
import me.javaroad.plugins.ui.SelectPathDialog;
import me.javaroad.plugins.util.TemplateUtils;

public class CodeGeneratorAction extends AnAction implements DumbAware {

    private TemplateSettings templateSettings;

    public CodeGeneratorAction() {
        this.templateSettings = ServiceManager.getService(TemplateSettings.class).getState();
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        Project project = anActionEvent.getProject();
        if (project == null) {
            return;
        }
        DumbService dumbService = DumbService.getInstance(project);
        if (dumbService.isDumb()) {
            dumbService.showDumbModeNotification("CodeGenerator plugin is not available during indexing");
            return;
        }

        SelectPathDialog dialog = new SelectPathDialog(project, templateSettings.getTemplateGroupMap());
        dialog.show();
        if (dialog.isOK()) {
            String basePackage = dialog.getBasePackage();
            String outputPath = dialog.getOutputPath();
            String templateGroup = dialog.getTemplateGroup();

            PsiFile psiFile = anActionEvent.getData(CommonDataKeys.PSI_FILE);
            if (Objects.isNull(psiFile) || !(psiFile instanceof PsiJavaFile)) {
                return;
            }

            PsiJavaFile psiJavaFile = (PsiJavaFile) psiFile;
            PsiClass[] psiClasses = psiJavaFile.getClasses();
            try {
                Map<String, Template> templateMap = templateSettings.getTemplateGroup(templateGroup).getTemplateMap();
                Entity entity = buildClassEntity(psiClasses[0]);
                TemplateUtils.generate(templateMap, entity, basePackage, outputPath);
            } catch (Exception e) {
                Messages.showMessageDialog(project, e.getMessage(), "Generate Failed", null);
                return;
            }
            Messages.showMessageDialog(project, "Code generation successful", "Success", null);
        }
    }


    private Entity buildClassEntity(PsiClass psiClass) {
        PsiFile psiFile = psiClass.getContainingFile();
        String className = psiClass.getName();
        String packageName = ((PsiClassOwner) psiFile).getPackageName();

        List<Field> fields = Arrays.stream(psiClass.getAllFields()).map(field -> {
            String fieldType = field.getType().getPresentableText();
            String fieldName = field.getName();
            return new Field(fieldType, fieldName);
        }).collect(Collectors.toList());

        return Entity.builder()
            .name(className)
            .packageName(packageName)
            .fields(fields).build();
    }
}
