package app.gui

import app.ProjectUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.GitRemoteBranch
import git4idea.repo.GitRepository
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class PurgeLocalBranchesDialog(project: Project?) : DialogWrapper(project) {

    companion object {
        private val log = Logger.getInstance(PurgeLocalBranchesDialog::class.java)
    }

    private lateinit var mainPanel: JPanel
    private lateinit var branchesTable: JTable
    private lateinit var projectsComboBox: JComboBox<ProjectInfo>

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        initProjectComboBox(project)
        init()
    }

    private fun initProjectComboBox(project: Project?) {
        val repositories = ProjectUtil.listRepositories(project)
        val projectInfos = repositories.map { ProjectInfo(it) }
        projectInfos.forEach { projectsComboBox.addItem(it) }

        updateBranchesTable(projectInfos[0])

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            updateBranchesTable(selectedProject as ProjectInfo)
        }
    }

    private fun updateBranchesTable(projectInfo: ProjectInfo) {
        branchesTable.model = BranchesTableModel(projectInfo)
        branchesTable.columnModel.getColumn(0).maxWidth = 30
        branchesTable.columnModel.getColumn(2).maxWidth = 30
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

}

class BranchesTableModel(private val projectInfo: ProjectInfo) : DefaultTableModel() {

    companion object {
        // like private static final in java
        private val COLUMN_CLASS = arrayOf(java.lang.Boolean::class.java, String::class.java, java.lang.Boolean::class.java)
        private val COLUMN_NAME = arrayOf("Select", "Branch", "Exist on remote")
    }

    init {
        COLUMN_NAME.forEach { addColumn(it) }

        val localBranches = projectInfo.gitRepo.branches.localBranches
        val remoteBranches: MutableCollection<GitRemoteBranch> = projectInfo.gitRepo.branches.remoteBranches
        localBranches.forEach { localBranch ->
            val isLocalBranchExistsOnRemote = remoteBranches.any {
                remoteBranch -> remoteBranch.nameForRemoteOperations == localBranch.name
            }

            addRow(arrayOf(!isLocalBranchExistsOnRemote, localBranch.name, isLocalBranchExistsOnRemote))
        }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

    override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]
}

data class ProjectInfo(val gitRepo: GitRepository) {
    override fun toString(): String {
        return gitRepo.root.name
    }
}