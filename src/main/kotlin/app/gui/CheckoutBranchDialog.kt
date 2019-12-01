package app.gui;

import app.ProjectUtil
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.MessageDialogBuilder
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vcs.changes.ChangeListManager
import com.intellij.openapi.vcs.changes.LocalChangeList
import com.intellij.ui.PopupMenuListenerAdapter
import com.intellij.util.ui.JBUI
import git4idea.GitLocalBranch
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository
import java.awt.Component
import java.awt.event.KeyAdapter
import java.awt.event.KeyEvent
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable
import javax.swing.event.PopupMenuEvent
import javax.swing.table.DefaultTableModel
import javax.swing.text.JTextComponent

class CheckoutBranchDialog(private val project: Project) : DialogWrapper(project) {

    companion object {
        private val log = Logger.getInstance(CheckoutBranchDialog::class.java)
    }

    private lateinit var mainPanel: JPanel
    private lateinit var reposTable: JTable
    private lateinit var branchComboBox: JComboBox<String>

    private val projectListTableModel: ProjectListTableModel = ProjectListTableModel(project)

    init {
        log.info("Project list dialog initialized for project $project")
        title = "Create branch"

        init()
    }

    override fun init() {
        initBranchComboBox()
        initReposTable()
        isOKActionEnabled = false
        super.init()
    }

    override fun show() {
        projectListTableModel.update();

        super.show()
    }

    private fun initBranchComboBox() {
        branchComboBox.toolTipText = "choose branch"
        branchComboBox.editor.editorComponent.addKeyListener(object: KeyAdapter() {
            override fun keyReleased(e: KeyEvent) {
                val component = e.source as Component
                val jComboBox = component.parent as JComboBox<*>
                val textComponent = jComboBox.editor.editorComponent as JTextComponent
                isOKActionEnabled = !textComponent.text.isEmpty()
            }
        })

        branchComboBox.addItemListener { e ->
            isOKActionEnabled = e.item != null
        }

        branchComboBox.addPopupMenuListener(object: PopupMenuListenerAdapter() {
            override fun popupMenuWillBecomeVisible(e: PopupMenuEvent?) {
                branchComboBox.removeAllItems()
                findCommonBranchesInRepos().forEach {
                    branchComboBox.addItem(it.name)
                }
            }
        })
    }

    private fun findCommonBranchesInRepos(): Set<GitLocalBranch> {
        val selectedRepos: Map<GitRepository, MutableCollection<GitLocalBranch>> = projectListTableModel
                .getSelectedRepos()
                .map { it to it.branches.localBranches }
                .toMap()

        var commonBranches = mutableSetOf<GitLocalBranch>();
        for (value in selectedRepos.values) {
            if (commonBranches.isEmpty()) {
                commonBranches.addAll(value)
            }

            commonBranches = commonBranches.intersect(value).toMutableSet()
        }

        return commonBranches
    }

    private fun initReposTable() {
        reposTable.model = projectListTableModel
        reposTable.autoCreateRowSorter = true
        reposTable.rowHeight = JBUI.scale(22)

        val columnModel = reposTable.columnModel
        val selectColumn = columnModel.getColumn(0)
        selectColumn.headerValue = "Select"
        selectColumn.maxWidth = 50

        val repoColumn = columnModel.getColumn(1)
        repoColumn.headerValue = "Repository"
        repoColumn.maxWidth = 250

        val branchColumn = columnModel.getColumn(2)
        branchColumn.headerValue = "Branch"
        branchColumn.maxWidth = 350
    }

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    override fun doOKAction() {
        val branchToCheckout = branchComboBox.selectedItem as String
        val checkoutBranchRes = MessageDialogBuilder.yesNo(
                "Checkout branch",
                "Checkout branch '$branchToCheckout'?"
        ).noText("Cancel").show()

        if (checkoutBranchRes == Messages.YES) {
            checkoutBranch(branchToCheckout)
            // close parent dialog
            super.doOKAction()
        }
    }

    private fun checkoutBranch(branchToCheckout: String) {
        val gitBrancher = GitBrancher.getInstance(project)
        for (selectedRepo in projectListTableModel.getSelectedRepos()) {
            val isNewBranch = selectedRepo.branches.findLocalBranch(branchToCheckout) == null
            if (isNewBranch) {
                gitBrancher.checkoutNewBranch(branchToCheckout, listOf(selectedRepo))
            } else {
                gitBrancher.checkout(branchToCheckout, false, listOf(selectedRepo), null)
            }
        }
    }

    class ProjectListTableModel(private val project: Project) : DefaultTableModel() {

        companion object {
            private val COLUMN_CLASS = arrayOf(java.lang.Boolean::class.java, String::class.java, String::class.java)
            private val COLUMN_NAME = arrayOf("Select", "Repository", "Branch")
        }

        init {
            COLUMN_NAME.forEach { addColumn(it) }
        }

        override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

        override fun getColumnClass(columnIndex: Int): Class<*> = COLUMN_CLASS[columnIndex]

        override fun getColumnCount(): Int = 3

        fun update() {
            dataVector.clear()
            val reposMap = getReposMap()
            val vcsRepositoryManager = VcsRepositoryManager.getInstance(project)
            val changeListManager = ChangeListManager.getInstance(project)
            reposMap.keys
                    .sorted()
                    .forEach {
                        val hasChanges = hasChanges(reposMap.getValue(it), vcsRepositoryManager, changeListManager)
                        addRow(arrayOf(hasChanges, it, reposMap[it]?.currentBranchName))
                    }
        }

        private fun hasChanges(
                repo: GitRepository,
                vcsRepositoryManager: VcsRepositoryManager,
                changeListManager: ChangeListManager
        ): Boolean {
            val curChangeList: LocalChangeList = changeListManager.changeLists.first { it.isDefault }

            return curChangeList.changes
                    .filter { it.virtualFile != null }
                    .map { vcsRepositoryManager.getRepositoryForFile(it.virtualFile!!, true) }
                    .any { it == repo }
        }

        fun getSelectedRepos(): MutableList<GitRepository> {
            val reposMap = getReposMap()
            val selectRepos = mutableListOf<GitRepository>()

            for (r in 0 until rowCount) {
                val isRepoSelected = getValueAt(r, 0) as Boolean
                if (isRepoSelected) {
                    val repoName = getValueAt(r, 1) as String
                    selectRepos.add(reposMap[repoName]!!)
                }
            }

            return selectRepos
        }

        private fun getReposMap() : Map<String, GitRepository> {
            return ProjectUtil.listRepositories(project).map { it.root.name to it }.toMap()
        }

    }

}
