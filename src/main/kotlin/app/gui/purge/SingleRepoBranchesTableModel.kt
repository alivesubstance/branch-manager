package app.gui.purge

import java.util.stream.IntStream
import kotlin.streams.asSequence

class SingleRepoBranchesTableModel : BranchesTableModel(
        arrayOf(java.lang.Boolean::class.java, String::class.java),
        arrayOf("Select", "Branch")
) {
    fun showBranches(gitRepoInfo: GitRepoInfo) {
        dataVector.clear()

        gitRepoInfo.localBranchesIgnoreCurrent.forEach { addRow(arrayOf(true, it.name)) }
    }

    fun getSelectedBranches(): Sequence<String> {
        return IntStream.range(0, rowCount)
                .asSequence()
                .filter { getValueAt(it, 0) as Boolean }
                .map { getValueAt(it, 1) as String }
    }

}