package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.content.Context
import android.content.res.Resources.NotFoundException
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat


/**
 * AHBottomNavigationItem
 * The item is display in the AHBottomNavigation layout
 */
class BottomNavigationItem {
    private var title = ""
    private var drawable: Drawable? = null
    private var color = Color.GRAY
    private var backGround = Color.GRAY

    @StringRes
    private var titleRes = 0

    @DrawableRes
    private var drawableRes = 0

    @ColorRes
    private var colorRes = 0

    /**
     * Constructor
     *
     * @param title    Title
     * @param resource Drawable resource
     */
    constructor(title: String, @DrawableRes resource: Int) {
        this.title = title
        drawableRes = resource
    }

    /**
     * @param title    Title
     * @param resource Drawable resource
     * @param color    Background color
     */
    constructor(title: String, @DrawableRes resource: Int, @ColorRes color: Int) {
        this.title = title
        drawableRes = resource
        this.color = color
    }

    /**
     * Constructor
     *
     * @param titleRes    String resource
     * @param drawableRes Drawable resource
     * @param colorRes    Color resource
     */
    constructor(@StringRes titleRes: Int, @DrawableRes drawableRes: Int, @ColorRes colorRes: Int) {
        this.titleRes = titleRes
        this.drawableRes = drawableRes
        this.colorRes = colorRes
    }

    /**
     * Constructor
     *
     * @param title    String
     * @param drawable Drawable
     */
    constructor(title: String, drawable: Drawable?) {
        this.title = title
        this.drawable = drawable
    }

    /**
     * Constructor
     *
     * @param title    String
     * @param drawable Drawable
     * @param color    Color
     */
    constructor(title: String, drawable: Drawable?, @ColorInt color: Int) {
        this.title = title
        this.drawable = drawable
        this.color = color
    }

    fun getTitle(context: Context): String {
        return if (titleRes != 0) {
            context.getString(titleRes)
        } else title
    }

    fun setTitle(title: String) {
        this.title = title
        titleRes = 0
    }

    fun setTitle(@StringRes titleRes: Int) {
        this.titleRes = titleRes
        title = ""
    }

    fun getColor(context: Context?): Int {
        return if (colorRes != 0) {
            ContextCompat.getColor(context!!, colorRes)
        } else color
    }

    fun setColor(@ColorInt color: Int) {
        this.color = color
        colorRes = 0
    }

    fun setColorRes(@ColorRes colorRes: Int) {
        this.colorRes = colorRes
        color = 0
    }

    fun getDrawable(context: Context?): Drawable? {
        return if (drawableRes != 0) {
            try {
                AppCompatResources.getDrawable(context!!, drawableRes)
            } catch (e: NotFoundException) {
                ContextCompat.getDrawable(context!!, drawableRes)
            }
        } else drawable
    }

    fun setDrawable(@DrawableRes drawableRes: Int) {
        this.drawableRes = drawableRes
        drawable = null
    }

    fun setDrawable(drawable: Drawable?) {
        this.drawable = drawable
        drawableRes = 0
    }
}
