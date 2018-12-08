package app.gui

import app.ProjectUtil
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import javax.swing.table.DefaultTableModel

class ProjectListTableModel(private val project: Project?) : DefaultTableModel() {

    companion object {
        // like private static final in java
        private val COLUMN_CLASS = arrayOf(Boolean::class.java, String::class.java, String::class.java)
        private val COLUMN_NAME = arrayOf(null, "Project", "Branch")
    }

    init {
        COLUMN_NAME.forEach { addColumn(it) }

        val activeRepositories: List<GitRepository> = ProjectUtil.listActiveRepositories(project)
        val rows = activeRepositories.map {
            Row(true, it.project.name, it.currentBranchName)
        }

        rows.forEach { addRow(it.toArray()) }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

    override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]
}

data class Row(val selected: Boolean, val project: String, val branch: String?) {
    fun toArray(): Array<out Any?> = arrayOf(selected, project, branch)
}