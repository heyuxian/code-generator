package me.javaroad.plugins.config;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.MasterDetailsStateService;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.ui.popup.ListPopupStep;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.tools.SimpleActionGroup;
import com.intellij.ui.AnActionButton.AnActionEventWrapper;
import com.intellij.util.IconUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.tree.TreePath;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;
import me.javaroad.plugins.settings.TemplateSettings;
import me.javaroad.plugins.ui.TemplateEditForm;
import me.javaroad.plugins.util.ZipFileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
public class TemplateEditorConfigurable extends MasterDetailsComponent implements SearchableConfigurable {

    private final Project project;
    private final TemplateSettings templateSettings;
    private final Map<String, TemplateGroup> templateGroupMap = Maps.newHashMap();

    public TemplateEditorConfigurable(Project project, TemplateSettings templateSettings) {
        this.project = project;
        this.templateSettings = templateSettings;
        initTree();
    }

    private void initTemplate() {
        templateGroupMap.clear();
        clearChildren();
        initTree();
        templateSettings.getTemplateGroupMap().forEach((groupName, group) -> {
            MyGroupNode parent = addGroupNode(groupName);
            group.getTemplateMap().forEach((templateName, template) -> {
                addTemplateNode(group, template, parent);
            });
            templateGroupMap.put(groupName, group);
        });
    }


    @Override
    public void apply() throws ConfigurationException {
        super.apply();
        templateSettings.save(templateGroupMap);
    }

    @Override
    public void reset() {
        initTemplate();
        super.reset();
    }

    @Override
    @Nullable
    protected ArrayList<AnAction> createActions(boolean fromPopup) {
        ArrayList<AnAction> result = new ArrayList<>();
        result.add(new DumbAwareAction("Add", "Add", IconUtil.getAddIcon()) {
            {
                registerCustomShortcutSet(CommonShortcuts.INSERT, myTree);
            }

            @Override
            public void actionPerformed(AnActionEvent event) {
                SimpleActionGroup myActionGroup = new SimpleActionGroup();
                myActionGroup.add(new DumbAwareAction("Add Template") {

                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        String templateName = Messages
                            .showInputDialog("Template Name", "Add Template", null, "", new InputValidator() {
                                @Override
                                public boolean checkInput(String input) {
                                    return StringUtils.isNotBlank(input) && checkTemplateName(input);
                                }

                                @Override
                                public boolean canClose(String input) {
                                    return checkInput(input);
                                }
                            });
                        if (StringUtils.isNotBlank(templateName)) {
                            createNewTemplate(templateName);
                        }
                    }
                });
                myActionGroup.add(new DumbAwareAction("Add Group") {

                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        String groupName = Messages
                            .showInputDialog("Group Name", "Add Group", null, "", new InputValidator() {
                                @Override
                                public boolean checkInput(String input) {
                                    return StringUtils.isNotBlank(input) && !templateGroupMap.containsKey(input);
                                }

                                @Override
                                public boolean canClose(String input) {
                                    return checkInput(input);
                                }
                            });
                        if (StringUtils.isNotBlank(groupName)) {
                            createNewGroup(groupName);
                        }
                    }
                });
                myActionGroup.add(new DumbAwareAction("Import") {

                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, false, true, true, false,
                            false);
                        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
                        if (Objects.nonNull(virtualFile)) {
                            Map<String, TemplateGroup> importTemplate = ZipFileUtils
                                .readTemplateFromFile(virtualFile.getPath());
                            if (Objects.nonNull(importTemplate)) {
                                importTemplate(importTemplate);
                            }
                        }
                    }
                });
                myActionGroup.add(new DumbAwareAction("Export") {

                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false,
                            false);
                        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
                        if (Objects.nonNull(virtualFile)) {
                            exportTemplate(virtualFile);
                        }
                    }
                });
                JBPopupFactory popupFactory = JBPopupFactory.getInstance();
                DataContext dataContext = event.getDataContext();
                ListPopupStep step = popupFactory.createActionsStep(myActionGroup, dataContext, false, false,
                    myActionGroup.getTemplatePresentation().getText(), getTree(), true, 0, true);
                ListPopup listPopup = popupFactory.createListPopup(step);
                listPopup.setHandleAutoSelectionBeforeShow(true);
                if (event instanceof AnActionEventWrapper) {
                    ((AnActionEventWrapper) event).showPopup(listPopup);
                } else {
                    listPopup.showInBestPositionFor(dataContext);
                }
            }
        });
        result.add(new MyDeleteAction() {
            @Override
            public void actionPerformed(AnActionEvent e) {
                int result = Messages.showOkCancelDialog("Confirm delete?", "Confirm", null);
                if (result == Messages.OK) {
                    removeNode();
                    super.actionPerformed(e);
                }
            }
        });
        return result;
    }

    private void exportTemplate(VirtualFile virtualFile) {
        ZipFileUtils
            .writeTemplateToFile(templateSettings.getTemplateGroupMap(), virtualFile.getPath());
        Messages.showInfoMessage("Export Successful", "Success");
    }

    private void importTemplate(Map<String, TemplateGroup> importTemplate) {
        int result = Messages.showYesNoDialog("This will overwrite the existing template, Do you confirm import it?",
            "Confirm Import", null);
        if (result == Messages.YES) {
            templateSettings.getTemplateGroupMap().clear();
            templateSettings.getTemplateGroupMap().putAll(importTemplate);
            initTemplate();
        }
    }

    private void createNewTemplate(String templateName) {
        MyGroupNode parent = getParentNode();
        if (Objects.isNull(parent)) {
            Messages.showWarningDialog("Please select a group", "Warning");
            return;
        }
        TemplateGroup group = templateGroupMap.get(parent.getDisplayName());

        Template template = new Template();
        template.setName(templateName);
        template.setContent("");
        addTemplateNode(group, template, parent);
        group.addTemplate(template);
    }

    private boolean checkTemplateName(String templateName) {
        MyGroupNode parent = getParentNode();
        if (Objects.nonNull(parent)) {
            TemplateGroup group = templateGroupMap.get(parent.getDisplayName());
            return !(group.exists(templateName));
        }
        return false;
    }

    private MyGroupNode getParentNode() {
        final TreePath selectionPath = myTree.getSelectionPath();
        if (Objects.nonNull(selectionPath)) {
            MyGroupNode parent;
            MyNode selectedNode = (MyNode) selectionPath.getLastPathComponent();

            if (selectedNode instanceof MyGroupNode) {
                parent = (MyGroupNode) selectedNode;
            } else {
                parent = (MyGroupNode) selectedNode.getParent();
            }
            return parent;
        }
        return null;
    }

    private void createNewGroup(String groupName) {
        addGroupNode(groupName);
        TemplateGroup templateGroup = new TemplateGroup();
        templateGroup.setName(groupName);
        templateGroupMap.put(templateGroup.getName(), templateGroup);
    }

    private void removeNode() {
        final TreePath selectionPath = myTree.getSelectionPath();
        if (Objects.nonNull(selectionPath)) {
            MyNode selectedNode = (MyNode) selectionPath.getLastPathComponent();
            if (selectedNode instanceof MyGroupNode) {
                templateGroupMap.remove(selectedNode.getDisplayName());
            } else {
                MyGroupNode parent = (MyGroupNode) selectedNode.getParent();
                templateGroupMap.get(parent.getDisplayName()).removeTemplate(selectedNode.getDisplayName());
            }
        }
    }


    private void addTemplateNode(TemplateGroup group, @NotNull Template template,
        @NotNull MyGroupNode parent) {
        MyNode myNode = new MyNode(new TemplateEditForm(group, template));
        addNode(myNode, parent);
        selectNodeInTree(myNode);
    }

    private MyGroupNode addGroupNode(String name) {
        MyGroupNode myGroupNode = new MyGroupNode(name);
        addNode(myGroupNode, myRoot);
        selectNodeInTree(myGroupNode);
        return myGroupNode;
    }

    @Override
    public void disposeUIResources() {
        super.disposeUIResources();
    }

    @Override
    protected void removePaths(TreePath... paths) {
        super.removePaths(paths);
    }

    @Override
    protected String getEmptySelectionString() {
        return "Select a template to view or edit its details here";
    }

    @Override
    protected MasterDetailsStateService getStateService() {
        return MasterDetailsStateService.getInstance(project);
    }


    @Override
    protected void processRemovedItems() {
    }

    @Override
    protected boolean wasObjectStored(Object o) {
        return true;
    }

    @Override
    @Nls
    public String getDisplayName() {
        return "Edit Template";
    }

    @NotNull
    @Override
    public String getId() {
        return "generator.editor.configurable";
    }

    @Override
    @NotNull
    @NonNls
    public String getHelpTopic() {
        return getId();
    }

    private static class MyGroupNode extends MyNode {

        MyGroupNode(String name) {
            super(new NamedConfigurable(false, null) {
                public void setDisplayName(String name) {
                }

                public Object getEditableObject() {
                    return null;
                }

                public String getBannerSlogan() {
                    return null;
                }

                public String getDisplayName() {
                    return name;
                }

                @Nullable
                @NonNls
                public String getHelpTopic() {
                    return null;
                }

                public JComponent createOptionsPanel() {
                    return new JLabel("Select a template to view or edit its details here", 0);
                }

                public boolean isModified() {
                    return false;
                }

                public void apply() throws ConfigurationException {
                }

                public void reset() {
                }

                public void disposeUIResources() {
                }

            }, false);
        }
    }
}
