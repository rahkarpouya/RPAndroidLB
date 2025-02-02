package ir.rahkarpouya.rpandroidlib.slider

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CirclePagerIndicatorDecoration(private val context: Context,
    private val colorActive: Int, private val colorInactive: Int
) :
    RecyclerView.ItemDecoration() {

    /**
     * Height of the space the indicator takes up at the bottom of the view.
     */
    private val mIndicatorHeight = (DP * 16).toInt()
    /**
     * Indicator stroke width.
     */
    private val mIndicatorStrokeWidth =
        DP * 4
    /**
     * Indicator width.
     */
    private val mIndicatorItemLength = DP * 4
    /**
     * Padding between indicators.
     */
    private val mIndicatorItemPadding =
        DP * 8
    /**
     * Some more natural animation interpolation
     */
    private val mInterpolator: Interpolator = AccelerateDecelerateInterpolator()
    private val mPaint: Paint = Paint()

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val itemCount = parent.adapter!!.itemCount
        // center horizontally, calculate width and subtract half from center
        val totalLength = mIndicatorItemLength * itemCount
        val paddingBetweenItems =
            0.coerceAtLeast(itemCount - 1) * mIndicatorItemPadding
        val indicatorTotalWidth = totalLength + paddingBetweenItems
        val indicatorStartX = (parent.width - indicatorTotalWidth) / 2f
        // center vertically in the allotted space
        val indicatorPosY = parent.height - mIndicatorHeight / 2f
        drawInactiveIndicators(c, indicatorStartX, indicatorPosY, itemCount)
        // find active page (which should be highlighted)
        val layoutManager = parent.layoutManager as LinearLayoutManager?
        val activePosition = layoutManager!!.findFirstVisibleItemPosition()
        if (activePosition == RecyclerView.NO_POSITION) {
            return
        }

        val activeChild: View? = layoutManager.findViewByPosition(activePosition)
        val left: Int = activeChild!!.left
        val width: Int = activeChild.width
//        val right: Int = activeChild.right

        val progress: Float = mInterpolator.getInterpolation(left * -1 / width.toFloat())
        drawHighlights(c, indicatorStartX, indicatorPosY, activePosition, progress)
    }

    private fun drawInactiveIndicators(
        c: Canvas,
        indicatorStartX: Float,
        indicatorPosY: Float,
        itemCount: Int
    ) {
        mPaint.color = ContextCompat.getColor(context,colorInactive)
        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding
        var start = indicatorStartX
        for (i in 0 until itemCount) {
            c.drawCircle(start, indicatorPosY, mIndicatorItemLength / 2f, mPaint)
            start += itemWidth
        }
    }

    private fun drawHighlights(
        c: Canvas, indicatorStartX: Float, indicatorPosY: Float,
        highlightPosition: Int, progress: Float
    ) {
        mPaint.color = ContextCompat.getColor(context,colorActive)
        // width of item indicator including padding
        val itemWidth = mIndicatorItemLength + mIndicatorItemPadding
        if (progress == 0f) { // no swipe, draw a normal indicator
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            c.drawCircle(highlightStart, indicatorPosY, mIndicatorItemLength / 2f, mPaint)
        } else {
            val highlightStart = indicatorStartX + itemWidth * highlightPosition
            // calculate partial highlight
            val partialLength =
                mIndicatorItemLength * progress + mIndicatorItemPadding * progress
            c.drawCircle(
                highlightStart + partialLength,
                indicatorPosY,
                mIndicatorItemLength / 2f,
                mPaint
            )
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        outRect.bottom = -8*3
    }

    companion object {
        private val DP: Float = Resources.getSystem().displayMetrics.density
    }

    init {
        mPaint.strokeWidth = mIndicatorStrokeWidth
        mPaint.style = Paint.Style.STROKE
        mPaint.isAntiAlias = true
    }
}