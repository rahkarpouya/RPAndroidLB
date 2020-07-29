package ir.rahkarpouya.rpandroidlib

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration

class RecycleDividerItemDecoration(
    context: Context, orientation: Int,
    private val marginLeftOrTop: Int = 20,
    private val marginRightOrBottom: Int = 20,
    private val reversLayout: Boolean = true
) : ItemDecoration() {

    private val mDivider: Drawable? = ContextCompat.getDrawable(context, R.drawable.line_divider)
    private var mOrientation = 0

    init {
        setOrientation(orientation)
    }

    private fun setOrientation(orientation: Int) {
        require(!(orientation != HORIZONTAL_LIST && orientation != VERTICAL_LIST)) { "invalid orientation" }
        mOrientation = orientation
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (mOrientation == VERTICAL_LIST)
            drawVertical(c, parent)
        else
            drawHorizontal(c, parent)

    }

    private fun drawVertical(c: Canvas, parent: RecyclerView) {
        val left = parent.paddingLeft + marginLeftOrTop
        val right = parent.width - parent.paddingRight - marginRightOrBottom
        val childCount = parent.childCount
        for (i in 0 until childCount - 1) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val top: Int = child.bottom + params.bottomMargin
            val bottom = top + mDivider!!.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    private fun drawHorizontal(c: Canvas, parent: RecyclerView) {
        val top = parent.paddingTop + marginLeftOrTop
        val bottom = parent.height - parent.paddingBottom - marginRightOrBottom

        val childCount = if (reversLayout) parent.childCount - 1 else parent.childCount
        for (i in 0 until childCount) {
            val child: View = parent.getChildAt(i)
            val params = child.layoutParams as RecyclerView.LayoutParams
            val left: Int = if (reversLayout)
                child.left + params.leftMargin
            else
                child.right + params.rightMargin
            val right = left + mDivider!!.intrinsicHeight
            mDivider.setBounds(left, top, right, bottom)
            mDivider.draw(c)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mDivider!!.intrinsicHeight)
        } else {
            outRect.set(0, 0, mDivider!!.intrinsicWidth, 0)
        }
    }

    companion object {
        const val HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL
        const val VERTICAL_LIST = LinearLayoutManager.VERTICAL
    }

}