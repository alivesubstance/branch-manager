package app.kt

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import git4idea.GitUtil
import javax.swing.JComponent


class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val activeProjects = ProjectUtil.listActiveProjects(e.project)
        println("activeProjects = $activeProjects")

//        val repositoryManager = GitUtil.getRepositoryManager(e.project!!)
//        repositoryManager.

        val builder = DialogBuilder(e.project)
        builder.setDimensionServiceKey("GrepConsoleTailFileDialog")
        builder.setTitle("Tail File settings")
//        builder.setCenterPanel()
        builder.removeAllActions()
        builder.addOkAction()
        builder.addCancelAction()
        builder.show()
    }


}