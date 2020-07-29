package ir.rahkarpouya.rpandroidlib.slider

import android.app.Activity
import android.view.animation.AnimationUtils
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class RPSliderView<T> {

    private var activity: Activity? = null
    private var recyclerView: RecyclerView? = null
    private var models: MutableList<T> = mutableListOf()
    private var reverseLayout: Boolean = false
    private var haveCard: Boolean = false
    private var haveTitle: Boolean = false
    private var holderSlider: SliderAdapter.SetSliderHolder<T>? = null
    private var timeNextPageSlider: Long = 4000
    private var colorActiveIndicatorSlider: Int = android.R.color.black
    private var colorInactiveIndicatorSlider: Int = android.R.color.darker_gray
    private var animationSlider: Int = 0
    private var setMarginItemSlider: Int = 8
    private var setRadiusCard: Float = 8f
    private var setElevation: Float = 3f

    fun setActivity(activity: Activity) = apply { this.activity = activity }

    fun setRecyclerView(recyclerView: RecyclerView) = apply { this.recyclerView = recyclerView }

    fun setModels(models: MutableList<T>) = apply { this.models = models }

    fun setReverseLayout(reverseLayout: Boolean) = apply { this.reverseLayout = reverseLayout }

    fun setHaveCard(haveCard: Boolean) = apply { this.haveCard = haveCard }

    fun setHaveTitle(haveTitle: Boolean) = apply { this.haveTitle = haveTitle }

    fun setHolderSlider(holderSlider: SliderAdapter.SetSliderHolder<T>) =
        apply { this.holderSlider = holderSlider }

    fun setTimeNextPageSlider(timeNextPageSlider: Long) =
        apply { this.timeNextPageSlider = timeNextPageSlider }

    fun setColorActiveIndicatorSlider(@ColorRes colorActiveIndicatorSlider: Int) =
        apply { this.colorActiveIndicatorSlider = colorActiveIndicatorSlider }

    fun setColorInactiveIndicatorSlider(@ColorRes colorInactiveIndicatorSlider: Int) =
        apply { this.colorInactiveIndicatorSlider = colorInactiveIndicatorSlider }

    fun setAnimationSlider(animationSlider: Int) =
        apply { this.animationSlider = animationSlider }

    fun setMarginItemSlider(setMarginItemSlider: Int) =
        apply { this.setMarginItemSlider = setMarginItemSlider }

    fun setRadiusCard(setRadiusCard: Float) =
        apply { this.setRadiusCard = setRadiusCard }

    fun setElevation(setElevation: Float) =
        apply { this.setElevation = setElevation }

    fun create() = recyclerView?.apply { setSlider(this) }

    private fun setSlider(recyclerView: RecyclerView) {
        recyclerView.layoutManager =
            LinearLayoutManager(activity, RecyclerView.HORIZONTAL, reverseLayout)

        recyclerView.addItemDecoration(
            CirclePagerIndicatorDecoration(
                activity!!,
                colorActiveIndicatorSlider,
                colorInactiveIndicatorSlider
            )
        )

        val slider = SliderAdapter(
            models,
            haveCard,
            haveTitle,
            setMarginItemSlider,
            setRadiusCard,
            setElevation
        )
        recyclerView.adapter = slider

        if (holderSlider != null)
            slider.getDataSlider(holderSlider!!)

        val snapHelper: SnapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        val speedScroll: Long = timeNextPageSlider
        val runnable = object : Runnable {
            var count = 1
            override fun run() {
                if (count == recyclerView.adapter!!.itemCount)
                    count = 0
                if (count < recyclerView.adapter!!.itemCount) {
                    recyclerView.smoothScrollToPosition(count++)
                    if (animationSlider != 0)
                        recyclerView.startAnimation(
                            AnimationUtils.loadAnimation(activity, animationSlider)
                        )
                    recyclerView.postDelayed(this, speedScroll)
                }
            }
        }
        recyclerView.postDelayed(runnable, speedScroll)
    }
}