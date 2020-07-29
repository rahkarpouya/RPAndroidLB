package ir.rahkarpouya.rpandroidlib.bottomNavigation.notification

import android.os.Parcelable
import android.text.TextUtils
import androidx.annotation.ColorInt
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Notification : Parcelable {
    @IgnoredOnParcel
    var text: String? = null
        private set

    @IgnoredOnParcel
    @ColorInt
    var textColor = 0
        // if 0 then use default value
        private set

    @IgnoredOnParcel
    @ColorInt
    var backgroundColor = 0
        // if 0 then use default value
        private set


    val isEmpty: Boolean
        get() = TextUtils.isEmpty(text)


    class Builder {
        private var text: String? = null

        @ColorInt
        private var textColor = 0

        @ColorInt
        private var backgroundColor = 0
        fun setText(text: String?): Builder {
            this.text = text
            return this
        }

        fun setTextColor(@ColorInt textColor: Int): Builder {
            this.textColor = textColor
            return this
        }

        fun setBackgroundColor(@ColorInt backgroundColor: Int): Builder {
            this.backgroundColor = backgroundColor
            return this
        }

        fun build(): Notification {
            val notification = Notification()
            notification.text = text
            notification.textColor = textColor
            notification.backgroundColor = backgroundColor
            return notification
        }
    }

    companion object {
        fun justText(text: String?): Notification {
            return Builder().setText(text).build()
        }

        fun generateEmptyList(size: Int): List<Notification> {
            val notificationList: MutableList<Notification> =
                ArrayList()
            for (i in 0 until size) {
                notificationList.add(Notification())
            }
            return notificationList
        }

    }
}
