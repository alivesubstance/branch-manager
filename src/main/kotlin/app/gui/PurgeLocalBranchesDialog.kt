package app.gui

import app.ProjectUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import git4idea.GitLocalBranch
import git4idea.GitRemoteBranch
import git4idea.GitUsagesTriggerCollector.Companion.reportUsage
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

class PurgeLocalBranchesDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private val log = Logger.getInstance(PurgeLocalBranchesDialog::class.java)
    }

    private var repositories: List<GitRepository>

    private lateinit var mainPanel: JPanel
    private lateinit var branchesTable: JTable
    private lateinit var projectsComboBox: JComboBox<GitRepoInfo>

    private val branchesTableModel = BranchesTableModel()

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        repositories = ProjectUtil.listRepositories(project)

        init()
    }

    override fun init() {
        initProjectComboBox()
        initBranchesTable()
        super.init()
    }

    private fun initProjectComboBox() {
        val projectInfos = repositories.map { GitRepoInfo(it) }
        projectInfos.forEach { projectsComboBox.addItem(it) }

        branchesTableModel.updateBranches(projectInfos[0])

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            branchesTableModel.updateBranches(selectedProject as GitRepoInfo)
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
        existOnRemoteColumn.headerValue = "Remote"
        existOnRemoteColumn.maxWidth = 50

        branchesTableModel.addTableModelListener { event ->
            if (event.firstRow != -1 && event.column != -1) {
                val gitRepoInfo = projectsComboBox.selectedItem as GitRepoInfo
                val isBranchSelected: Boolean = branchesTableModel.getValueAt(event.firstRow, event.column) as Boolean
                if (isBranchSelected) {
                    gitRepoInfo.removeCandidates.add(gitRepoInfo.localBranches[event.firstRow])
                }
            }
        }
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    override fun doOKAction() {
        val deleteLocalBranchesRes = MessageDialogBuilder.yesNo(
                "Delete local branches",
                "Are you sure to delete local branches?"
        ).noText("Cancel").show()

        if (deleteLocalBranchesRes == Messages.YES) {
            deleteLocalBranches(projectsComboBox.selectedItem as GitRepoInfo)
            // close parent dialog
            super.doOKAction()
        }
    }

    private fun deleteLocalBranches(gitRepoInfo: GitRepoInfo) {
        val gitBrancher = GitBrancher.getInstance(project)

        gitRepoInfo.removeCandidates.forEach {
            gitBrancher.deleteBranch(it.name, mutableListOf(gitRepoInfo.gitRepo))
            reportUsage(project, "git.branch.delete.local")
        }
    }

    class BranchesTableModel : DefaultTableModel() {

        companion object {
            // like private static final in java
            private val COLUMN_CLASS = arrayOf(java.lang.Boolean::class.java, String::class.java, java.lang.Boolean::class.java)
            private val COLUMN_NAME = arrayOf("Select", "Branch", "Remote")
        }

        init {
            COLUMN_NAME.forEach { addColumn(it) }
        }

        override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

        override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]

        override fun getColumnCount(): Int = 3

        fun updateBranches(gitRepoInfo: GitRepoInfo) {
            clearData(gitRepoInfo)

            gitRepoInfo.localBranches.forEach { localBranch ->
                val isCurrentLocalBranch = gitRepoInfo.gitRepo.currentBranch === localBranch
                val isLocalBranchExistsOnRemote = gitRepoInfo.remoteBranches.any { remoteBranch ->
                            remoteBranch.nameForRemoteOperations == localBranch.name
                        }

                if (!isCurrentLocalBranch && !isLocalBranchExistsOnRemote) {
                    gitRepoInfo.removeCandidates.add(localBranch)
                }

                addRow(arrayOf(!isCurrentLocalBranch && !isLocalBranchExistsOnRemote, localBranch.name, isLocalBranchExistsOnRemote))
            }
        }

        private fun clearData(gitRepoInfo: GitRepoInfo) {
            dataVector.clear()
            gitRepoInfo.removeCandidates.clear()
        }
    }

    data class GitRepoInfo(val gitRepo: GitRepository) {

        var removeCandidates = mutableListOf<GitLocalBranch>()

        val localBranches: List<GitLocalBranch>
            get() = gitRepo.branches.localBranches.toList()

        val remoteBranches: List<GitRemoteBranch>
            get() = gitRepo.branches.remoteBranches.toList()

        override fun toString(): String {
            return gitRepo.root.name
        }

    }

}

