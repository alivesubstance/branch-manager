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
import one.util.streamex.IntStreamEx
import java.awt.Component
import java.awt.Font
import java.util.stream.IntStream
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.DefaultTableModel
import kotlin.streams.asSequence

class PurgeLocalBranchesDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private val log = Logger.getInstance(PurgeLocalBranchesDialog::class.java)
    }

    private var repositories: List<GitRepoInfo>

    private lateinit var mainPanel: JPanel
    private lateinit var branchesTable: JTable
    private lateinit var projectsComboBox: JComboBox<GitRepoInfo>

    private val branchesTableModel = BranchesTableModel()
    private val tableCellRenderer = MyDefaultTableCellRenderer()

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        repositories = ProjectUtil.listRepositories(project).map { GitRepoInfo(it) }
        init()
    }

    override fun init() {
        initProjectComboBox()
        initBranchesTable()
        super.init()
    }

    private fun initProjectComboBox() {
        repositories.forEach { projectsComboBox.addItem(it) }

        updateBranches(repositories[0])

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            val repoInfo = selectedProject as GitRepoInfo
            updateBranches(repoInfo)
        }
    }

    private fun updateBranches(gitRepoInfo: GitRepoInfo) {
        branchesTableModel.updateBranches(gitRepoInfo)
        tableCellRenderer.currentRepo = gitRepoInfo
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

        branchesTable.setDefaultRenderer(String::class.java, tableCellRenderer)

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

        branchesTableModel.getSelectedBranches().forEach {
            gitBrancher.deleteBranch(it, mutableListOf(gitRepoInfo.gitRepo))
            reportUsage(project, "git.branch.delete.local")
        }
    }

    class MyDefaultTableCellRenderer: DefaultTableCellRenderer() {
        lateinit var currentRepo: GitRepoInfo

        override fun getTableCellRendererComponent(
                table: JTable?, value: Any?, isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int
        ): Component {
            val component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column)
            if (column == 1 && currentRepo.gitRepo.currentBranch?.name == value) {
                component.font = component.font.deriveFont(Font.BOLD)
            }
            return component
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
            dataVector.clear()

            gitRepoInfo.localBranches
                    .sortedBy { localBranch -> localBranch.name }
                    .forEach { localBranch ->
                        val isCurrentLocalBranch = gitRepoInfo.gitRepo.currentBranch === localBranch
                        val isLocalBranchExistsOnRemote = gitRepoInfo.remoteBranches.any { remoteBranch ->
                            remoteBranch.nameForRemoteOperations == localBranch.name
                        }

                addRow(arrayOf(!isCurrentLocalBranch && !isLocalBranchExistsOnRemote, localBranch.name, isLocalBranchExistsOnRemote))
            }
        }

        fun getSelectedBranches(): Sequence<String> {
            return IntStream.range(0, rowCount)
                    .asSequence()
                    .filter { getValueAt(it, 0) as Boolean }
                    .map { getValueAt(it, 1) as String }
        }
    }

    data class GitRepoInfo(val gitRepo: GitRepository) {

        val localBranches: List<GitLocalBranch>
            get() = gitRepo.branches.localBranches.toList()

        val remoteBranches: List<GitRemoteBranch>
            get() = gitRepo.branches.remoteBranches.toList()

        override fun toString(): String {
            return gitRepo.root.name
        }

    }

}

