package app.gui.purge

import javax.swing.table.DefaultTableModel

open class BranchesTableModel(
        private val columnClass: Array<Class<out Any>>,
        private val columnName: Array<String>
): DefaultTableModel() {

    init {
        columnName.forEach { addColumn(it) }
    }

    override fun isCellEditable(row: Int, column: Int): Boolean = column == 0

    override fun getColumnClass(columnIndex: Int): Class<*> = columnClass[columnIndex]

    override fun getColumnCount(): Int = columnName.size

}