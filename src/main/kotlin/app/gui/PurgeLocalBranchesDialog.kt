package app.gui

import app.ProjectUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.util.ui.JBUI
import git4idea.GitLocalBranch
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

    private val branchesTableModel = BranchesTableModel()

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        initBranchesTable()
        initProjectComboBox(project)
        init()
    }

    private fun initProjectComboBox(project: Project?) {
        val repositories = ProjectUtil.listRepositories(project)
        val projectInfos = repositories.map { ProjectInfo(it) }
        projectInfos.forEach { projectsComboBox.addItem(it) }

        branchesTableModel.updateBranches(projectInfos[0])

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            branchesTableModel.updateBranches(selectedProject as ProjectInfo)
        }
    }

    private fun initBranchesTable() {
        branchesTable.model = branchesTableModel
        branchesTable.rowHeight = JBUI.scale(22)

        val columnModel = branchesTable.columnModel
        val selectColumn = columnModel.getColumn(0)
        selectColumn.headerValue = "Select"
        selectColumn.maxWidth = 50

        val branchColumn = columnModel.getColumn(1)
        branchColumn.headerValue = "Branch"

        val existOnRemoteColumn = columnModel.getColumn(2)
        existOnRemoteColumn.headerValue = "Exist on remote"
        existOnRemoteColumn.maxWidth = 150
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }
}

class BranchesTableModel: DefaultTableModel() {

    var removeCandidates = mutableListOf<GitLocalBranch>()

    companion object {
        // like private static final in java
        private val COLUMN_CLASS = arrayOf(java.lang.Boolean::class.java, String::class.java, java.lang.Boolean::class.java)
        private val COLUMN_NAME = arrayOf("Select", "Branch", "Exist on remote")
    }

    init {
        COLUMN_NAME.forEach { addColumn(it) }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

    override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]

    override fun getColumnCount(): Int  = 3

    fun updateBranches(projectInfo: ProjectInfo) {
        clearTable()

        val localBranches: MutableCollection<GitLocalBranch> = projectInfo.gitRepo.branches.localBranches
        val remoteBranches: MutableCollection<GitRemoteBranch> = projectInfo.gitRepo.branches.remoteBranches
        localBranches.forEach { localBranch ->
            val isLocalBranchExistsOnRemote = remoteBranches.any {
                remoteBranch -> remoteBranch.nameForRemoteOperations == localBranch.name
            }

            if (!isLocalBranchExistsOnRemote) {
                removeCandidates.add(localBranch)
            }

            addRow(arrayOf(!isLocalBranchExistsOnRemote, localBranch.name, isLocalBranchExistsOnRemote))
        }
    }

    private fun clearTable() {
        if (rowCount > 0) {
            for (i in 0..rowCount) {
                removeRow(i)
            }
        }

        removeCandidates.clear()
    }
}

data class ProjectInfo(val gitRepo: GitRepository) {
    override fun toString(): String {
        return gitRepo.root.name
    }
}