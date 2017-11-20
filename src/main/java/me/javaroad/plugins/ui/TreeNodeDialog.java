package me.javaroad.plugins.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import me.javaroad.plugins.model.TreeNode;
import me.javaroad.plugins.model.TreeNode.Type;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.Nullable;

/**
 * @author heyx
 */
public class TreeNodeDialog extends DialogWrapper {

    private JPanel mainPanel;
    private JComboBox<TreeNode.Type> nodeTypeCombo;
    private JTextField nodeNameField;

    public TreeNodeDialog(Project project) {
        super(project);
        setTitle("Add New Node");
        initCombo();
        init();
    }

    private void initCombo() {
        nodeTypeCombo.addItem(Type.GROUP);
        nodeTypeCombo.addItem(Type.TEMPLATE);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    @Override
    protected void doOKAction() {
        if (checkInput(nodeNameField.getText())) {
            close(0);
        } else {
            nodeNameField.requestFocus();
        }
    }

    private boolean checkInput(String inputString) {
        return StringUtils.isNotBlank(inputString);
    }

    public TreeNode getTreeNode() {
        TreeNode.Type type = (Type) nodeTypeCombo.getSelectedItem();
        String name = nodeNameField.getText();
        return new TreeNode(type, name);
    }
}
