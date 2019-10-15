package app.gui.purge

import app.ProjectUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import git4idea.branch.GitBrancher
import javax.swing.*
import javax.swing.table.TableRowSorter

class PurgeLocalBranchesDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private const val ALL_REPO = "All Projects"
        private val log = Logger.getInstance(PurgeLocalBranchesDialog::class.java)
    }

    private var repositories: List<GitRepoInfo>

    private lateinit var mainPanel: JPanel
    private lateinit var branchesTable: JTable
    private lateinit var projectsComboBox: JComboBox<GitRepoInfo>

    private val singleRepoModel = SingleRepoBranchesTableModel()
    private val multiReposModel = MultiReposBranchesTableModel()

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        repositories = ProjectUtil.listRepositories(project)
                .map { GitRepoInfo(it) }
                .sortedBy { it.name }
        init()
    }

    override fun init() {
        initProjectComboBox()
        super.init()
    }

    private fun initProjectComboBox() {
        val allRepoGitInfo = GitRepoInfo(ALL_REPO)
        projectsComboBox.addItem(allRepoGitInfo)
        repositories.forEach { projectsComboBox.addItem(it) }

        updateBranches(allRepoGitInfo)

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            val repoInfo = selectedProject as GitRepoInfo
            updateBranches(repoInfo)
        }
    }

    private fun updateBranches(gitRepoInfo: GitRepoInfo) {
        if (gitRepoInfo.name == ALL_REPO) {
            showMultiRepoBranchesTable()
        } else {
            showSingleRepoBranchesTable(gitRepoInfo)
        }
    }

    private fun showMultiRepoBranchesTable() {
        val multiReposModelSorter = TableRowSorter(multiReposModel)
        multiReposModelSorter.sortKeys = listOf(
                RowSorter.SortKey(1, SortOrder.ASCENDING),
                RowSorter.SortKey(2, SortOrder.ASCENDING)
        )

        branchesTable.model = multiReposModel
        branchesTable.rowSorter = multiReposModelSorter
        branchesTable.rowHeight = JBUI.scale(22)

        val columnModel = branchesTable.columnModel
        val selectColumn = columnModel.getColumn(0)
        selectColumn.headerValue = "Select"
        selectColumn.maxWidth = 50

        val repoColumn = columnModel.getColumn(1)
        repoColumn.headerValue = "Repository"
        repoColumn.maxWidth = 250

        val branchColumn = columnModel.getColumn(2)
        branchColumn.headerValue = "Branch"
        branchColumn.maxWidth = 350

        multiReposModel.showBranches(project)
    }

    private fun showSingleRepoBranchesTable(gitRepoInfo: GitRepoInfo) {
        val singleRepoModelSorter = TableRowSorter(singleRepoModel)
        singleRepoModelSorter.sortKeys = listOf(RowSorter.SortKey(1, SortOrder.ASCENDING))

        branchesTable.model = singleRepoModel
        branchesTable.rowSorter = singleRepoModelSorter
        branchesTable.rowHeight = JBUI.scale(22)

        val columnModel = branchesTable.columnModel
        val selectColumn = columnModel.getColumn(0)
        selectColumn.headerValue = "Select"
        selectColumn.maxWidth = 50

        val branchColumn = columnModel.getColumn(1)
        branchColumn.headerValue = "Branch"

        singleRepoModel.showBranches(gitRepoInfo)
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

        if (gitRepoInfo.name == ALL_REPO) {
            multiReposModel.getSelectedBranches(project).forEach { k, v ->
                deleteBranch(gitBrancher, k, v)
            }
        } else {
            singleRepoModel.getSelectedBranches().forEach {
                deleteBranch(gitBrancher, gitRepoInfo, it)
            }
        }
    }

    private fun deleteBranch(gitBrancher: GitBrancher, gitRepoInfo: GitRepoInfo, vararg branches: String) {
        branches.forEach {
            gitBrancher.deleteBranch(it, mutableListOf(gitRepoInfo.gitRepo))
        }
    }

}

