package app.gui

import java.awt.Component
import java.awt.event.ItemEvent
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import javax.swing.*
import javax.swing.table.JTableHeader
import javax.swing.table.TableCellRenderer

internal class CheckBoxHeader(table: JTable) : JCheckBox(), TableCellRenderer, MouseListener {

    var rendererComponent: CheckBoxHeader = this
    var column = 0
    var mousePressed = false

    init {
        rendererComponent = this
        rendererComponent.horizontalAlignment = SwingConstants.CENTER
        rendererComponent.addItemListener {
            fun itemStateChanged(e: ItemEvent) {
                val source = e.source
                if (source !is AbstractButton) return

                val checked = e.stateChange == ItemEvent.SELECTED
                var x = 0
                val y: Int = table.rowCount
                while (x < y) {
                    table.setValueAt(checked, x, 0)
                    x++
                }
            }
        }
    }

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

}