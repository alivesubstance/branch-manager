package app

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager


object ProjectUtil {

    fun listActiveProjects(project: Project?): List<String> = ProjectRootManager.getInstance(project!!).contentRootUrls

}