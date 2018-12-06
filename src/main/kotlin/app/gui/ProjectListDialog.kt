package app.gui

import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.table.JBTable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable

// Marking a constructor parameter as a val turns it into a property of a class
class ProjectListDialog(private val project: Project?) : DialogWrapper(project) {
    private val log = Logger.getInstance(ProjectListDialog::class.java)

    private lateinit var mainPanel: JPanel
    private lateinit var mainTable: JTable

    init {
        log.info("Project list dialog initialized for project $project")
        title = "Create branch"

        init()
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    private fun createUIComponents() {
        mainTable = JBTable(ProjectListTableModel(project))
    }

}
