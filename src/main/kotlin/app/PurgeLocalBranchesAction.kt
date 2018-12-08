package app

import app.gui.ProjectListDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val projectListDialog = ProjectListDialog(e.project)
        projectListDialog.showAndGet()
    }

}