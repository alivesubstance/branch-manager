package app.gui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.table.JBTable
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class ProjectListDialog(project: Project?) : DialogWrapper(project) {
    private lateinit var mainPanel: JPanel
    private lateinit var mainTable: JTable

    init {
        title = "Projects"
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    private fun createUIComponents() {
        val tableModel = DefaultTableModel(null, arrayOf<Any>("", "Project", "Branch"))

        mainTable = JBTable(tableModel)
    }

}
