package app.kt

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PurgeLocalBranchesAction: AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val activeProjects = ProjectUtil.listActiveProjects(e.project)
        println("activeProjects = $activeProjects")
    }

}