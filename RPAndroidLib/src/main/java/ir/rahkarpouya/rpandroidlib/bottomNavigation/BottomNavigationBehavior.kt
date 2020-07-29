package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.animation.ObjectAnimator
import android.os.Build
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import android.view.animation.Interpolator
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorCompat
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout

class BottomNavigationBehavior<V : View?> : VerticalScrollingBehavior<V>() {
    private var mTabLayoutId = 0

    /**
     * Is hidden
     * @return
     */
    var isHidden = false
        private set
    private var translationAnimator: ViewPropertyAnimatorCompat? = null
    private var translationObjectAnimator: ObjectAnimator? = null
    private var mTabLayout: TabLayout? = null
    private var snackbarLayout: Snackbar.SnackbarLayout? = null
    private var mSnackbarHeight = -1
    private var navigationBarHeight = 0
    private var targetOffset = 0f
    private val snackBarY = 0f
    private var behaviorTranslationEnabled = true
    private var navigationPositionListener: BottomNavigation.OnNavigationPositionListener? = null




    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        val layoutChild: Boolean = super.onLayoutChild(parent, child, layoutDirection)
        if (mTabLayout == null && mTabLayoutId != View.NO_ID) {
            mTabLayout = findTabLayout(child!!)
        }
        return layoutChild
    }

    private fun findTabLayout(child: View): TabLayout? {
        return if (mTabLayoutId == 0) null else child.findViewById<View>(mTabLayoutId) as TabLayout
    }

    override fun layoutDependsOn(parent: CoordinatorLayout, child: V, dependency: View): Boolean {
        if (dependency is Snackbar.SnackbarLayout) {
            updateSnackbar(child!!, dependency)
            return true
        }
        return super.layoutDependsOn(parent, child, dependency)
    }

    override fun onNestedVerticalOverScroll(
        coordinatorLayout: CoordinatorLayout?,
        child: V,
        @ScrollDirection direction: Int,
        currentOverScroll: Int,
        totalOverScroll: Int
    ) {
    }

    override fun onDirectionNestedPreScroll(
        coordinatorLayout: CoordinatorLayout?,
        child: V,
        target: View?,
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        @ScrollDirection scrollDirection: Int
    ) {
    }

    override fun onNestedDirectionFling(
        coordinatorLayout: CoordinatorLayout?,
        child: V,
        target: View?,
        velocityX: Float,
        velocityY: Float,
        @ScrollDirection scrollDirection: Int
    ): Boolean {
        return false
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        super.onNestedScroll(
            coordinatorLayout,
            child,
            target,
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            type,
            consumed
        )
        if (dyConsumed < 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_DOWN)
        } else if (dyConsumed > 0) {
            handleDirection(child, ScrollDirection.SCROLL_DIRECTION_UP)
        }
    }

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        axes: Int,
        type: Int
    ): Boolean {
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL || super.onStartNestedScroll(
            coordinatorLayout,
            child,
            directTargetChild,
            target,
            axes,
            type
        )
    }

    /**
     * Handle scroll direction
     * @param child
     * @param scrollDirection
     */
    private fun handleDirection(child: V, scrollDirection: Int) {
        if (!behaviorTranslationEnabled) {
            return
        }
        if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_DOWN && isHidden) {
            isHidden = false
            animateOffset(child, 0, forceAnimation = false, withAnimation = true)
        } else if (scrollDirection == ScrollDirection.SCROLL_DIRECTION_UP && !isHidden) {
            isHidden = true
            animateOffset(child, child!!.height, forceAnimation = false, withAnimation = true)
        }
    }

    /**
     * Animate offset
     *
     * @param child
     * @param offset
     */
    private fun animateOffset(
        child: V,
        offset: Int,
        forceAnimation: Boolean,
        withAnimation: Boolean
    ) {
        if (!behaviorTranslationEnabled && !forceAnimation) {
            return
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            ensureOrCancelObjectAnimation(child, offset, withAnimation)
            translationObjectAnimator!!.start()
        } else {
            ensureOrCancelAnimator(child, withAnimation)
            translationAnimator!!.translationY(offset.toFloat()).start()
        }
    }

    /**
     * Manage animation for Android >= KITKAT
     *
     * @param child
     */
    private fun ensureOrCancelAnimator(child: V, withAnimation: Boolean) {
        if (translationAnimator == null) {
            translationAnimator = ViewCompat.animate(child!!)
            translationAnimator!!.duration = (if (withAnimation) ANIM_DURATION else 0L)
            translationAnimator!!.setUpdateListener { view ->
                if (navigationPositionListener != null) {
                    navigationPositionListener!!.onPositionChange((view.measuredHeight - view.translationY + snackBarY).toInt())
                }
            }
            translationAnimator!!.interpolator = INTERPOLATOR
        } else {
            translationAnimator!!.duration = (if (withAnimation) ANIM_DURATION else 0L)
            translationAnimator!!.cancel()
        }
    }

    /**
     * Manage animation for Android < KITKAT
     *
     * @param child
     */
    private fun ensureOrCancelObjectAnimation(
        child: V,
        offset: Int,
        withAnimation: Boolean
    ) {
        if (translationObjectAnimator != null) {
            translationObjectAnimator!!.cancel()
        }
        translationObjectAnimator = ObjectAnimator.ofFloat(
            child,
            View.TRANSLATION_Y,
            offset.toFloat()
        )
        translationObjectAnimator!!.duration = (if (withAnimation) ANIM_DURATION else 0.toLong())
        translationObjectAnimator!!.interpolator = INTERPOLATOR
        translationObjectAnimator!!.addUpdateListener {
            if (snackbarLayout != null && snackbarLayout!!.layoutParams is MarginLayoutParams) {
                targetOffset = child!!.measuredHeight - child.translationY
                val p =
                    snackbarLayout!!.layoutParams as MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetOffset.toInt())
                snackbarLayout!!.requestLayout()
            }
            // Pass navigation height to listener
            if (navigationPositionListener != null) {
                navigationPositionListener!!.onPositionChange((child!!.measuredHeight - child.translationY + snackBarY).toInt())
            }
        }
    }

    /**
     * Enable or not the behavior translation
     * @param behaviorTranslationEnabled
     */
    fun setBehaviorTranslationEnabled(
        behaviorTranslationEnabled: Boolean,
        navigationBarHeight: Int
    ) {
        this.behaviorTranslationEnabled = behaviorTranslationEnabled
        this.navigationBarHeight = navigationBarHeight
    }

    /**
     * Set OnNavigationPositionListener
     */
    fun setOnNavigationPositionListener(navigationHeightListener: BottomNavigation.OnNavigationPositionListener?) {
        navigationPositionListener = navigationHeightListener
    }

    /**
     * Remove OnNavigationPositionListener()
     */
    fun removeOnNavigationPositionListener() {
        navigationPositionListener = null
    }

    /**
     * Hide AHBottomNavigation with animation
     * @param view
     * @param offset
     */
    fun hideView(view: V, offset: Int, withAnimation: Boolean) {
        if (!isHidden) {
            isHidden = true
            animateOffset(view, offset, true, withAnimation)
        }
    }

    /**
     * Reset AHBottomNavigation position with animation
     * @param view
     */
    fun resetOffset(view: V, withAnimation: Boolean) {
        if (isHidden) {
            isHidden = false
            animateOffset(view, 0, true, withAnimation)
        }
    }

    /**
     * Update Snackbar bottom margin
     */
    private fun updateSnackbar(child: View, dependency: View?) {
        if (dependency != null && dependency is Snackbar.SnackbarLayout) {
            snackbarLayout = dependency
            if (mSnackbarHeight == -1) {
                mSnackbarHeight = dependency.height
            }
            val targetMargin = (child.measuredHeight - child.translationY).toInt()
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                child.bringToFront()
            }
            if (dependency.layoutParams is MarginLayoutParams) {
                val p = dependency.layoutParams as MarginLayoutParams
                p.setMargins(p.leftMargin, p.topMargin, p.rightMargin, targetMargin)
                dependency.requestLayout()
            }
        }
    }

    companion object {
        private val INTERPOLATOR: Interpolator =
            LinearOutSlowInInterpolator()
        private const val ANIM_DURATION = 300L
//        fun <V : View?> from(view: V): AHBottomNavigationBehavior<V> {
//            val params = view!!.layoutParams
//            require(params is CoordinatorLayout.LayoutParams) { "The view is not a child of CoordinatorLayout" }
//            val behavior: CoordinatorLayout.Behavior = params.behavior
//            require(behavior is AHBottomNavigationBehavior<*>) { "The view is not associated with AHBottomNavigationBehavior" }
//            return behavior
//        }
    }
}