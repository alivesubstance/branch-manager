package app.gui.purge

import app.ProjectUtil
import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import com.intellij.openapi.project.Project
import java.util.stream.IntStream
import kotlin.streams.asSequence

class MultiReposBranchesTableModel : BranchesTableModel(
        arrayOf(java.lang.Boolean::class.java, String::class.java, String::class.java),
        arrayOf("Select", "Repository", "Branch")
) {
    fun showBranches(project: Project) {
        dataVector.clear()

        ProjectUtil.listRepositories(project)
                .map { GitRepoInfo(it) }
                .forEach { gitRepoInfo ->
                    gitRepoInfo.localBranchesIgnoreCurrent.forEach {
                        addRow(arrayOf(true, gitRepoInfo.name, it.name))
                    }
                }
    }

    fun getSelectedBranches(project: Project): Multimap<GitRepoInfo, String> {
        val gitRepoByName = ProjectUtil.listRepositories(project)
                .map { GitRepoInfo(it) }
                .map { it.name to it }
                .toMap()

        val branchesToGitRepoInfo = ArrayListMultimap.create<GitRepoInfo, String>()
        IntStream.range(0, rowCount)
                .asSequence()
                .filter { getValueAt(it, 0) as Boolean }
                .forEach {
                    val repoName = getValueAt(it, 1) as String
                    val branchName = getValueAt(it, 2) as String
                    branchesToGitRepoInfo.put(gitRepoByName[repoName], branchName)
                }

        return branchesToGitRepoInfo
    }
}