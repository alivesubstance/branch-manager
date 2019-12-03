package app.gui

import java.awt.Component
import java.awt.event.ItemListener
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.JCheckBox
import javax.swing.JTable
import javax.swing.SwingConstants
import javax.swing.UIManager
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellRenderer

internal class CheckBoxHeader(itemListener: ItemListener?) : JCheckBox(), TableCellRenderer, MouseListener {

    var rendererComponent: CheckBoxHeader = this
    var column = 0
    var mousePressed = false
    override fun getTableCellRendererComponent(
            table: JTable, value: Any,
            isSelected: Boolean, hasFocus: Boolean, row: Int, column: Int): Component {
        if (table != null) {
            val header = table.tableHeader
            if (header != null) {
                rendererComponent.foreground = header.foreground
                rendererComponent.background = header.background
                rendererComponent.font = header.font
                header.addMouseListener(rendererComponent)
            }
        }

        border = UIManager.getBorder("TableHeader.cellBorder")
        return rendererComponent
    }

    protected fun handleClickEvent(e: MouseEvent) {
        if (mousePressed) {
            mousePressed = false
            val header = e.source as JTableHeader
            val tableView = header.table
            val columnModel = tableView.columnModel
            val viewColumn = columnModel.getColumnIndexAtX(e.x)
            val column = tableView.convertColumnIndexToModel(viewColumn)
            if (viewColumn == this.column && e.clickCount == 1 && column != -1) {
                doClick()
            }
        }
    }

    override fun mouseClicked(e: MouseEvent) {
        handleClickEvent(e)
        (e.source as JTableHeader).repaint()
    }

    override fun mousePressed(e: MouseEvent) {
        mousePressed = true
    }

    override fun mouseReleased(e: MouseEvent) {}
    override fun mouseEntered(e: MouseEvent) {}
    override fun mouseExited(e: MouseEvent) {}

    init {
        rendererComponent.addItemListener(itemListener)
        rendererComponent.horizontalAlignment = SwingConstants.CENTER
    }
}