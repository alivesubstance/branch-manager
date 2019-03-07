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

class PurgeLocalBranchesDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private const val ALL_REPO = "All Projects"
        private val log = Logger.getInstance(PurgeLocalBranchesDialog::class.java)
    }

    private var repositories: List<GitRepoInfo>

    private lateinit var mainPanel: JPanel
    private lateinit var branchesTable: JTable
    private lateinit var projectsComboBox: JComboBox<GitRepoInfo>

    private val branchesTableModel = SingleRepoBranchesTableModel()

    init {
        title = "Purge local branches"
        setOKButtonText("Purge")

        repositories = ProjectUtil.listRepositories(project)
                .map { GitRepoInfo(it) }
                .sortedBy { it.repoName }
        init()
    }

    override fun init() {
        initProjectComboBox()
        initBranchesTable()
        super.init()
    }

    private fun initProjectComboBox() {
        projectsComboBox.addItem(GitRepoInfo(ALL_REPO))
        repositories.forEach { projectsComboBox.addItem(it) }

//        updateBranches(repositories[0])

        projectsComboBox.addActionListener { event ->
            val selectedProject = (event.source as JComboBox<*>).selectedItem
            val repoInfo = selectedProject as GitRepoInfo
            updateBranches(repoInfo)
        }
    }

    private fun updateBranches(gitRepoInfo: GitRepoInfo) {
        branchesTableModel.updateBranches(gitRepoInfo)
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

    class MultipleRepoBranchesTableModel : BranchesTableModel(
            arrayOf(java.lang.Boolean::class.java, String::class.java, String::class.java),
            arrayOf("Select", "Repository", "Branch")
    ) {
        init {
            addRow(arrayOf(true, "repo1", "branch1"))
            addRow(arrayOf(true, "repo1", "branch2"))
            addRow(arrayOf(true, "repo2", "branch1"))
        }
    }

    class GitRepoInfo {

        private var myRepoName: String? = null

        // internal, it is visible everywhere in the same module
        // see https://kotlinlang.org/docs/reference/visibility-modifiers.html#modules
        internal var gitRepo: GitRepository? = null

        constructor(repoName: String) {
            myRepoName = repoName
        }

        constructor(repo: GitRepository) {
            gitRepo = repo
        }

        val localBranches: List<GitLocalBranch>
            get() = gitRepo!!.branches.localBranches.toList()

        val remoteBranches: List<GitRemoteBranch>
            get() = gitRepo!!.branches.remoteBranches.toList()

        val currentBranch: GitLocalBranch
            get() = gitRepo!!.currentBranch!!

        val repoName: String
            get() = gitRepo!!.root.name

        override fun toString(): String {
            return if (gitRepo != null) { gitRepo!!.root.name } else myRepoName!!
        }

    }

}

