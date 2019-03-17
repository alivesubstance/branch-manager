package app.gui.purge

import git4idea.GitLocalBranch
import git4idea.GitRemoteBranch
import git4idea.repo.GitRepository

class GitRepoInfo {

    private var myName: String? = null

    // internal, it is visible everywhere in the same module
    // see https://kotlinlang.org/docs/reference/visibility-modifiers.html#modules
    internal var gitRepo: GitRepository? = null

    constructor(name: String) {
        myName = name
    }

    constructor(repo: GitRepository) {
        gitRepo = repo
    }

    val localBranchesIgnoreCurrent: List<GitLocalBranch>
        get() = gitRepo!!.branches.localBranches
                .sortedBy { localBranch -> localBranch.name }
                // ignore remove branches
                .filter { !remoteBranches.any { remoteBranch -> remoteBranch.nameForRemoteOperations == it.name } }
                // ignore current branch
                .filter { currentBranch != it }


    val name: String
        get() = if (gitRepo != null) { gitRepo!!.root.name } else myName!!

    private val remoteBranches: List<GitRemoteBranch>
        get() = gitRepo!!.branches.remoteBranches.toList()

    private val currentBranch: GitLocalBranch
        get() = gitRepo!!.currentBranch!!

    override fun toString(): String {
        return name
    }

}