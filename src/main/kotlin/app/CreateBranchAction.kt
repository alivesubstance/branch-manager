package app

import app.gui.CheckoutBranchDialog2
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class CreateBranchAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        CheckoutBranchDialog2(e.project!!).showAndGet()

    }

}