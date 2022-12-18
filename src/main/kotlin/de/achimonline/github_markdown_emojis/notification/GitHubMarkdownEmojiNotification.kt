package de.achimonline.github_markdown_emojis.notification

import com.intellij.notification.NotificationGroupManager
import com.intellij.notification.NotificationType
import com.intellij.openapi.project.Project

class GitHubMarkdownEmojiNotification {
    fun notifyError(
        project: Project?,
        content: String?
    ) {
        NotificationGroupManager.getInstance()
            .getNotificationGroup("GITHUB_MARKDOWN_EMOJIS")
            .createNotification(content!!, NotificationType.ERROR)
            .notify(project)
    }
}
