package app

import com.intellij.openapi.project.Project
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager


object ProjectUtil {

    fun listRepositories(project: Project?): List<GitRepository> =
        GitRepositoryManager.getInstance(project!!).repositories
}