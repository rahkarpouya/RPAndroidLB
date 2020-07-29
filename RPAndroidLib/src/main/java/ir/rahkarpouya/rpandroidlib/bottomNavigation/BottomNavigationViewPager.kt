package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class BottomNavigationViewPager : ViewPager {

    private var isPagingEnabled = false

    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)

    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> return isPagingEnabled
            MotionEvent.ACTION_UP -> {
                performClick()
                return isPagingEnabled
            }
        }
        return false
    }

    override fun performClick(): Boolean {
        return isPagingEnabled && super.performClick()
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return isPagingEnabled && super.onInterceptTouchEvent(event)
    }

    fun setPagingEnabled(b: Boolean) {
        isPagingEnabled = b
    }
}