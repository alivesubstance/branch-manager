package app.gui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ProjectListDialog extends DialogWrapper {
    private JPanel mainPanel;
    private JTable mainTable;

    public ProjectListDialog(@Nullable Project project) {
        super(project);
        setTitle("Projects");

        createUIComponents();
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
