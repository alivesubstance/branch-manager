package app.gui

import app.ProjectUtil
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import java.util.*
import javax.swing.table.DefaultTableModel

class ProjectListTableModel(private val project: Project?) : DefaultTableModel() {

    private val COLUMN_CLASS = arrayOf(Boolean::class.java, String::class.java, String::class.java)
    private val COLUMN_NAME = arrayOf(null, "Project", "Branch")

    init {
        COLUMN_NAME.forEach { addColumn(it) }

        val activeProjects = ProjectUtil.listActiveProjects(project)
        addRow(arrayOf(true, "test project 1", "test branch "))
        addRow(arrayOf(false, "test project 2", "test branch "))

    }

    override fun isCellEditable(row: Int, column: Int): Boolean = column == 1

    override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]

    fun getCurrentProjectBranch(virtualFile: VirtualFile) {


        val handler = GitLineHandler(/*myProject*/, /*root*/, GitCommand.BRANCH)
        handler.setSilent(true)
        handler.addParameters("--no-color", "-a", "--no-merged")
        val output = Git.getInstance().runCommand(handler).getOutputOrThrow()
        val lines = StringTokenizer(output, "\n", false)
        while (lines.hasMoreTokens()) {
            val branch = lines.nextToken().substring(2)

        }
    }
}

data class Row(val selected: Boolean, val project: VirtualFile, val branch: String)