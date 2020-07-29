package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat
import kotlin.math.roundToInt

object Helper {

    fun getTintDrawable(
        drawable: Drawable,
        @ColorInt color: Int
    ): Drawable {
        val wrapDrawable = DrawableCompat.wrap(drawable).mutate()
        DrawableCompat.setTint(wrapDrawable, color)
        return wrapDrawable
    }

    fun updateTopMargin(view: View, fromMargin: Int, toMargin: Int) {
        val animator =
            ValueAnimator.ofFloat(fromMargin.toFloat(), toMargin.toFloat())
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            if (view.layoutParams is MarginLayoutParams) {
                val p = view.layoutParams as MarginLayoutParams
                p.setMargins(p.leftMargin, animatedValue.toInt(), p.rightMargin, p.bottomMargin)
                view.requestLayout()
            }
        }
        animator.start()
    }

    /**
     * Update left margin with animation
     */
    fun updateLeftMargin(view: View, fromMargin: Int, toMargin: Int) {
        val animator =
            ValueAnimator.ofFloat(fromMargin.toFloat(), toMargin.toFloat())
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            if (view.layoutParams is MarginLayoutParams) {
                val p = view.layoutParams as MarginLayoutParams
                p.setMargins(animatedValue.toInt(), p.topMargin, p.rightMargin, p.bottomMargin)
                view.requestLayout()
            }
        }
        animator.start()
    }

    /**
     * Update text size with animation
     */
    fun updateTextSize(
        textView: TextView,
        fromSize: Float,
        toSize: Float
    ) {
        val animator = ValueAnimator.ofFloat(fromSize, toSize)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, animatedValue)
        }
        animator.start()
    }

    /**
     * Update alpha
     */
    fun updateAlpha(
        view: View,
        fromValue: Float,
        toValue: Float
    ) {
        val animator = ValueAnimator.ofFloat(fromValue, toValue)
        animator.duration = 150
        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            view.alpha = animatedValue
        }
        animator.start()
    }

    /**
     * Update text color with animation
     */
    fun updateTextColor(
        textView: TextView, @ColorInt fromColor: Int,
        @ColorInt toColor: Int
    ) {
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator -> textView.setTextColor((animator.animatedValue as Int)) }
        colorAnimation.start()
    }

    fun updateBackColor(
        container: FrameLayout, @ColorInt fromColor: Int,
        @ColorInt toColor: Int
    ) {
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator -> container.setBackgroundColor((animator.animatedValue as Int)) }
        colorAnimation.start()
    }

    /**
     * Update image view color with animation
     */
    fun updateDrawableColor(
        drawable: Drawable, imageView: ImageView,
        @ColorInt fromColor: Int, @ColorInt toColor: Int
    ) {
        val colorAnimation =
            ValueAnimator.ofObject(ArgbEvaluator(), fromColor, toColor)
        colorAnimation.duration = 150
        colorAnimation.addUpdateListener { animator ->
            imageView.setImageDrawable(
                getTintDrawable(
                    drawable,
                    animator.animatedValue as Int
                )
            )
            imageView.requestLayout()
        }
        colorAnimation.start()
    }

    /**
     * Update width
     */
    fun updateWidth(
        view: View,
        fromWidth: Float,
        toWidth: Float
    ) {
        val animator = ValueAnimator.ofFloat(fromWidth, toWidth)
        animator.duration = 150
        animator.addUpdateListener { anim ->
            val params = view.layoutParams
            params.width = (anim.animatedValue as Float).roundToInt()
            view.layoutParams = params
        }
        animator.start()
    }
}
