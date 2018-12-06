package app.gui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComponent

class PurgeLocalBranchesDialog protected constructor(project: Project?) : DialogWrapper(project) {

    override fun createCenterPanel(): JComponent? {
        return null
    }

}
