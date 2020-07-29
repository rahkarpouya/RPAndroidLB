package ir.rahkarpouya.rpandroidlib.bottomNavigation.notification

import androidx.annotation.ColorInt

object NotificationHelper {
    /**
     * Get text color for given notification. If color is not set (0), returns default value.
     *
     * @param notification     AHNotification, non null
     * @param defaultTextColor int default text color for all notifications
     * @return
     */
    fun getTextColor(notification: Notification, @ColorInt defaultTextColor: Int): Int {
        val textColor: Int = notification.textColor
        return if (textColor == 0) defaultTextColor else textColor
    }

    /**
     * Get background color for given notification. If color is not set (0), returns default value.
     *
     * @param notification           AHNotification, non null
     * @param defaultBackgroundColor int default background color for all notifications
     * @return
     */
    fun getBackgroundColor(
        notification: Notification,
        @ColorInt defaultBackgroundColor: Int
    ): Int {
        val backgroundColor: Int = notification.backgroundColor
        return if (backgroundColor == 0) defaultBackgroundColor else backgroundColor
    }
}
