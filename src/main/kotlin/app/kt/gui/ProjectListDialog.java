package app.kt.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import git4idea.i18n.GitBundle;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ProjectListDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable mainTable;

    public ProjectListDialog(@Nullable Project project) {
        super(project);
        setTitle("Projects");
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    private void createUIComponents() {
        DefaultTableModel tableModel = new DefaultTableModel(null, new Object[]{"", "Project", "Branch"});
        mainTable.setModel(tableModel);
    }

}
