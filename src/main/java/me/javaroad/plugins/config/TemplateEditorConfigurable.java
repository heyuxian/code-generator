package me.javaroad.plugins.config;

import com.google.common.collect.Maps;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MasterDetailsComponent;
import com.intellij.openapi.ui.MasterDetailsStateService;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.NamedConfigurable;
import com.intellij.util.IconUtil;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.tree.TreePath;
import me.javaroad.plugins.model.Template;
import me.javaroad.plugins.model.TemplateGroup;
import me.javaroad.plugins.model.TreeNode;
import me.javaroad.plugins.model.TreeNode.Type;
import me.javaroad.plugins.settings.TemplateSettings;
import me.javaroad.plugins.ui.TemplateEditForm;
import me.javaroad.plugins.ui.TreeNodeDialog;
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
        myRoot.removeAllChildren();
        templateGroupMap.clear();
        myRoot.removeAllChildren();
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
                TreeNodeDialog dialog = new TreeNodeDialog(project);
                dialog.show();
                if (dialog.isOK()) {
                    TreeNode treeNode = dialog.getTreeNode();
                    createNewNode(treeNode);
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

    private void createNewNode(TreeNode treeNode) {
        if (Type.GROUP.equals(treeNode.getType())) {
            if (templateGroupMap.containsKey(treeNode.getName())) {
                Messages.showWarningDialog("Group " + treeNode.getName() + " already exists", "Warning");
                return;
            }
            addGroupNode(treeNode.getName());
            TemplateGroup templateGroup = new TemplateGroup();
            templateGroup.setName(treeNode.getName());
            templateGroupMap.put(templateGroup.getName(), templateGroup);
        } else {
            final TreePath selectionPath = myTree.getSelectionPath();
            if (Objects.nonNull(selectionPath)) {
                MyGroupNode parent;
                MyNode selectedNode = (MyNode) selectionPath.getLastPathComponent();

                if (selectedNode instanceof MyGroupNode) {
                    parent = (MyGroupNode) selectedNode;
                } else {
                    parent = (MyGroupNode) selectedNode.getParent();
                }
                TemplateGroup group = templateGroupMap.get(parent.getDisplayName());
                if (group.exists(treeNode.getName())) {
                    Messages.showWarningDialog("Template " + treeNode.getName() + " already exists", "Warning");
                    return;
                }
                Template template = new Template();
                template.setName(treeNode.getName());
                template.setContent("");
                addTemplateNode(group, template, parent);
                group.addTemplate(template);
            } else {
                Messages.showWarningDialog("Please select a group", "Warning");
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
