package app

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.vfs.VirtualFile


object ProjectUtil {

    fun listActiveProjects(project: Project?): Array<out VirtualFile> =
            ProjectRootManager.getInstance(project!!).contentRoots

    fun listActiveRepositories(project: Project?): List<GitRepository> =
        GitRepositoryManager.getInstance(project!!).repositories
}