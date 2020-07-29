package ir.rahkarpouya.rpandroidlib.slider

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import ir.rahkarpouya.rpandroidlib.R

class SliderAdapter<T>(
    private val models: MutableList<T>,
    private val haveCard: Boolean = false,
    private val haveTitle: Boolean = true,
    private val setMarginItemSlider: Int,
    private val setRadiusCard: Float,
    private val setElevation: Float
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var sliderHolder: SetSliderHolder<T>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return SliderHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_slider, parent, false)
        )
    }

    override fun getItemCount(): Int = models.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val sliderAdapter = holder as SliderAdapter<*>.SliderHolder
        sliderHolder.setDataSlider(models[position], sliderAdapter)
    }

    inner class SliderHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var cardSlider: CardView = itemView.findViewById(R.id.cardSlider)
        var itemSliderImage: AppCompatImageView = itemView.findViewById(R.id.item_slider_image)
        var itemSliderTitle: AppCompatTextView = itemView.findViewById(R.id.item_slider_title)
        var itemSliderProgress: ProgressBar = itemView.findViewById(R.id.item_slider_progress)

        init {

            val layoutParams = cardSlider.layoutParams as ViewGroup.MarginLayoutParams

            if (haveCard) {
                cardSlider.cardElevation = setElevation
                cardSlider.maxCardElevation = setElevation
                cardSlider.radius = setRadiusCard
                layoutParams.setMargins(
                    setMarginItemSlider * 3,
                    setMarginItemSlider * 3,
                    setMarginItemSlider * 3,
                    setMarginItemSlider * 3
                )
            } else {
                cardSlider.cardElevation = 0f
                cardSlider.maxCardElevation = 0f
                cardSlider.radius = 0f
            }
            cardSlider.requestLayout()

            if (haveTitle)
                itemSliderTitle.visibility = View.VISIBLE
            else
                itemSliderTitle.visibility = View.GONE
        }
    }

    interface SetSliderHolder<T> {
        fun setDataSlider(data: T, holder: SliderAdapter<*>.SliderHolder)
    }

    fun getDataSlider(data: SetSliderHolder<T>) {
        this.sliderHolder = data
    }

}