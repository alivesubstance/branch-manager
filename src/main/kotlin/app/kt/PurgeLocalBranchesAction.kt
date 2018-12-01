package app.kt

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.ui.DialogBuilder
import javax.swing.JComponent


class PurgeLocalBranchesAction: AnAction() {

    private var jCheckBox1: javax.swing.JCheckBox? = null
    private var jCheckBox2: javax.swing.JCheckBox? = null
    private var jInternalFrame1: javax.swing.JInternalFrame? = null
    private var jLabel1: javax.swing.JLabel? = null
    private var jLabel2: javax.swing.JLabel? = null
    private var jScrollPane1: javax.swing.JScrollPane? = null

    override fun actionPerformed(e: AnActionEvent) {
        val activeProjects = ProjectUtil.listActiveProjects(e.project)
        println("activeProjects = $activeProjects")

        val builder = DialogBuilder(e.project)
        builder.setDimensionServiceKey("GrepConsoleTailFileDialog")
        builder.setTitle("Tail File settings")
        builder.setCenterPanel(createSwingPanel())
        builder.removeAllActions()
        builder.addOkAction()
        builder.addCancelAction()
        builder.show()
    }

    private fun createSwingPanel(): JComponent? {
        jScrollPane1 = javax.swing.JScrollPane()
        jInternalFrame1 = javax.swing.JInternalFrame()
        jCheckBox1 = javax.swing.JCheckBox()
        jLabel1 = javax.swing.JLabel()
        jCheckBox2 = javax.swing.JCheckBox()
        jLabel2 = javax.swing.JLabel()

        jInternalFrame1!!.setVisible(true)

        jCheckBox1!!.setText("jCheckBox1")

        jLabel1!!.setText("jLabel1")

        jCheckBox2!!.setText("jCheckBox2")

        jLabel2!!.setText("jLabel2")

        val jInternalFrame1Layout = javax.swing.GroupLayout(jInternalFrame1!!.getContentPane())
        jInternalFrame1!!.getContentPane().setLayout(jInternalFrame1Layout)
        jInternalFrame1Layout.setHorizontalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                                .addComponent(jCheckBox1)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel1))
                                        .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                                .addComponent(jCheckBox2)
                                                .addGap(18, 18, 18)
                                                .addComponent(jLabel2)))
                                .addGap(0, 249, java.lang.Short.MAX_VALUE.toInt()))
        )
        jInternalFrame1Layout.setVerticalGroup(
                jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jInternalFrame1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jCheckBox1)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jInternalFrame1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jCheckBox2)
                                        .addComponent(jLabel2))
                                .addContainerGap(241, java.lang.Short.MAX_VALUE.toInt()))
        )

        jScrollPane1!!.setViewportView(jInternalFrame1)

        return jScrollPane1;

    }

}