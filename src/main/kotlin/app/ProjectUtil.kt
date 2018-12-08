package app

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager


object ProjectUtil {

    fun listContentRoots(project: Project?): Array<out VirtualFile> =
            ProjectRootManager.getInstance(project!!).contentRoots

    fun listRepositories(project: Project?): List<GitRepository> =
        GitRepositoryManager.getInstance(project!!).repositories
}