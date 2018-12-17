package app

import app.gui.PurgeLocalBranchesDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent


class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val purgeLocalBranchesDialog = PurgeLocalBranchesDialog(e.project!!)
        purgeLocalBranchesDialog.showAndGet()
    }

}