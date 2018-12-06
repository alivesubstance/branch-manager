package app

import app.gui.ProjectListDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val activeProjects = ProjectUtil.listActiveProjects(e.project)
        println("activeProjects = $activeProjects")

        val projectListDialog = ProjectListDialog(e.project)
        projectListDialog.showAndGet()

//        val builder = DialogBuilder(e.project)
//        builder.setDimensionServiceKey("GrepConsoleTailFileDialog")
//        builder.setTitle("Tail File settings")
//        builder.setCenterPanel()
//        builder.removeAllActions()
//        builder.addOkAction()
//        builder.addCancelAction()
//        builder.show()
    }


}