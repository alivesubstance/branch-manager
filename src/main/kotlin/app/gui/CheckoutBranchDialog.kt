package app.gui;

import app.ProjectUtil
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.util.ui.JBUI
import git4idea.GitUsagesTriggerCollector
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.JTextField
import javax.swing.table.DefaultTableModel

class CheckoutBranchDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private val log = Logger.getInstance(CheckoutBranchDialog::class.java)
    }

    private lateinit var mainPanel: JPanel
    private lateinit var reposTable: JTable
    private lateinit var branchTextField: JTextField

    private val reposMap: MutableMap<String, GitRepository> =
            ProjectUtil.listRepositories(project).map { it.root.name to it }.toMap().toMutableMap()

    private val selectedProjects = mutableListOf<GitRepository>()

    init {
        log.info("Project list dialog initialized for project $project")
        title = "Create branch"

        init()
    }

    override fun init() {
        initReposTable()
        super.init()
    }

    private fun initReposTable() {
        val tableModel = ProjectListTableModel(reposMap)
        reposTable.model = tableModel
        reposTable.rowHeight = JBUI.scale(22)

        val columnModel = reposTable.columnModel
        val selectColumn = columnModel.getColumn(0)
        selectColumn.headerValue = "Select"
        selectColumn.maxWidth = 50

        val branchColumn = columnModel.getColumn(1)
        branchColumn.headerValue = "Repository"
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    override fun doOKAction() {
        val branchToCheckout = branchTextField.text
        val checkoutBranchRes = MessageDialogBuilder.yesNo(
                "Checkout branch",
                "Are you sure to checkout branch '$branchToCheckout'?"
        ).noText("Cancel").show()

        if (checkoutBranchRes == Messages.YES) {
            checkoutBranch(branchToCheckout)
            // close parent dialog
            super.doOKAction()
        }
    }

    private fun checkoutBranch(branchToCheckout: String) {
        GitUsagesTriggerCollector.reportUsage(project, "git.branch.create.new")
        val gitBrancher = GitBrancher.getInstance(project)
        gitBrancher.checkoutNewBranch(
                branchToCheckout,
                getSelectRepositories()
        )
    }

    private fun getSelectRepositories(): MutableList<GitRepository> {
        val selectRepos = mutableListOf<GitRepository>()

        val tableModel = reposTable.model
        for (r in 0 until tableModel.rowCount) {
            val isRepoSelected = tableModel.getValueAt(r, 0) as Boolean
            if (isRepoSelected) {
                val repoName = tableModel.getValueAt(r, 1) as String
                selectRepos.add(reposMap[repoName]!!)
            }
        }

        return selectRepos
    }

    class ProjectListTableModel(private val reposMap: Map<String, GitRepository>) : DefaultTableModel() {

        companion object {
            private val COLUMN_CLASS = arrayOf(java.lang.Boolean::class.java, String::class.java)
            private val COLUMN_NAME = arrayOf("Select", "Project")
        }

        init {
            COLUMN_NAME.forEach { addColumn(it) }

            reposMap.keys
                    .sorted()
                    .forEach { addRow(arrayOf(false, it)) }
        }

        override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

        override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]

        override fun getColumnCount(): Int = 2
    }

}
