package app

import app.gui.purge.PurgeLocalBranchesDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        PurgeLocalBranchesDialog(e.project!!).showAndGet()
    }

}