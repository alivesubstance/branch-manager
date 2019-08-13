package app

import app.gui.CheckoutBranchDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class OpenCheckoutBranchDialogAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        CheckoutBranchDialog(e.project!!).showAndGet()
    }

}
