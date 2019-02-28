package app.gui

import java.awt.Component
import java.awt.Container
import java.awt.Graphics
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*
import javax.swing.table.DefaultTableCellRenderer
import javax.swing.table.JTableHeader

class CheckableHeaderRenderer(private val header: JTableHeader) : DefaultTableCellRenderer() {

    private val checkbox = JCheckBox()

    init {
        header.addMouseListener(object : MouseAdapter() {

            override fun mouseClicked(e: MouseEvent?) {
                val table = (e!!.source as JTableHeader).table
                val columnModel = table.columnModel
                val viewColumn = columnModel.getColumnIndexAtX(e.getX())
                val modelColumn = table.convertColumnIndexToModel(viewColumn)
                if (modelColumn == 0) {
                    val isSelected = !checkbox.isSelected
                    checkbox.isSelected = isSelected
                    for (i in 0 until table.model.rowCount) {
                        table.model.setValueAt(isSelected, i, 0)
                    }
                    (e.source as JTableHeader).repaint()
                }
            }
        })
    }

    override fun getTableCellRendererComponent(
            tbl: JTable?, obj: Any, isS: Boolean, hasF: Boolean, row: Int, col: Int): Component {
        val r = tbl!!.tableHeader.defaultRenderer
        val l = r.getTableCellRendererComponent(tbl, obj, isS, hasF, row, col) as JLabel
        l.icon = CheckBoxIcon(checkbox)
        return l
    }

    private class CheckBoxIcon(private val check: JCheckBox) : Icon {

        override fun getIconWidth(): Int {
            return check.preferredSize.width
        }

        override fun getIconHeight(): Int {
            return check.preferredSize.height
        }

        override fun paintIcon(c: Component, g: Graphics, x: Int, y: Int) {
            SwingUtilities.paintComponent(
                    g, check, c as Container, x, y, iconWidth, iconHeight)
        }
    }
}