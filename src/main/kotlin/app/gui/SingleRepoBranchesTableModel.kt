package app.gui

import java.util.stream.IntStream
import kotlin.streams.asSequence

class SingleRepoBranchesTableModel : BranchesTableModel(
        arrayOf(java.lang.Boolean::class.java, String::class.java),
        arrayOf("Select", "Branch")
) {

    fun updateBranches(gitRepoInfo: PurgeLocalBranchesDialog.GitRepoInfo) {
        dataVector.clear()

        gitRepoInfo.localBranches
                .sortedBy { localBranch -> localBranch.name }
                // ignore remove branches
                .filter { !gitRepoInfo.remoteBranches.any { remoteBranch -> remoteBranch.nameForRemoteOperations == it.name } }
                // ignore current branch
                .filter { gitRepoInfo.currentBranch != it }
                .forEach { addRow(arrayOf(true, it.name)) }
    }

    fun getSelectedBranches(): Sequence<String> {
        return IntStream.range(0, rowCount)
                .asSequence()
                .filter { getValueAt(it, 0) as Boolean }
                .map { getValueAt(it, 1) as String }
    }

}