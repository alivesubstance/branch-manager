package app.kt

import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ProjectRootManager
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile


object ProjectUtil {

    fun listActiveProjects(project: Project?): List<String> = ProjectRootManager.getInstance(project!!).contentRootUrls

}