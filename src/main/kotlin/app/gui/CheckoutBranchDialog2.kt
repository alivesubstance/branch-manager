package app.gui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTable

class CheckoutBranchDialog2(private val project: Project) : DialogWrapper(project) {

    override fun createCenterPanel(): JComponent? {
        return mainPanel
    }

    private lateinit var mainPanel: JPanel
    private lateinit var comboBox1: JComboBox<*>
    private lateinit var comboBox2: JComboBox<*>
    private lateinit var table1: JTable

    fun setData(data: CheckoutBranchDialog2) {}

    fun getData(data: CheckoutBranchDialog2) {}

    fun isModified(data: CheckoutBranchDialog2): Boolean {
        return false
    }
}
