package ir.rahkarpouya.rpandroidlib.bottomNavigation

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import ir.rahkarpouya.rpandroidlib.bottomNavigation.notification.Notification
import ir.rahkarpouya.rpandroidlib.bottomNavigation.notification.NotificationHelper
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ir.rahkarpouya.rpandroidlib.R
import java.util.*

class BottomNavigation : FrameLayout {

    // Listener
    private var tabSelectedListener: OnTabSelectedListener? = null
    private var navigationPositionListener: OnNavigationPositionListener? = null

    // Variables
    private val items: ArrayList<BottomNavigationItem> = ArrayList()
    private val views: ArrayList<View> = ArrayList()
    private lateinit var bottomNavigationBehavior: BottomNavigationBehavior<BottomNavigation>
    private lateinit var linearLayoutContainer: LinearLayout
    private lateinit var backgroundColorView: View
    private var selectedBackgroundVisible: Boolean = false

    private var isTranslucentNavigationEnabled: Boolean = false
    private var notifications: MutableList<Notification?> =
        Notification.generateEmptyList(MAX_ITEMS).toMutableList()
    private val itemsEnabledStates: Array<Boolean> = arrayOf(true, true, true, true, true)
    private var isBehaviorTranslationSet: Boolean = false
    private var currentItem: Int = 0
    private var currentColor: Int = 0
    private var behaviorTranslationEnabled: Boolean = true
    private var needHideBottomNavigation: Boolean = false
    private var hideBottomNavigationWithAnimation: Boolean = false
    private var soundEffectsEnabled: Boolean = true

    // Variables (Styles)
    private var titleTypeface: Typeface? = null
    private var defaultBackgroundColor: Int = Color.WHITE
    private var defaultBackgroundResource: Int = 0

    @ColorInt
    private var itemActiveColor: Int = 0

    @ColorInt
    private var itemInactiveColor: Int = 0

    @ColorInt
    private var titleColorActive: Int = 0

    @ColorInt
    private var itemDisableColor: Int = 0

    @ColorInt
    private var titleColorInactive: Int = 0

    @ColorInt
    private var coloredTitleColorActive: Int = 0

    @ColorInt
    private var coloredTitleColorInactive: Int = 0


    private var titleActiveTextSize: Float = 0f
    private var titleInactiveTextSize: Float = 0f
    private var bottomNavigationHeight: Int = 0
    private var navigationBarHeight: Int = 0
    private var selectedItemWidth: Float = 0f
    private var notSelectedItemWidth: Float = 0f
    private var titleState: TitleState = TitleState.SHOW_WHEN_ACTIVE

    // Notifications
    @ColorInt
    private var notificationTextColor: Int = 0

    @ColorInt
    private var notificationBackgroundColor: Int = 0


    private var notificationBackgroundDrawable: Drawable? = null
    private var notificationTypeface: Typeface? = null
    private var notificationActiveMarginLeft: Int = 0
    private var notificationInactiveMarginLeft: Int = 0
    private var notificationActiveMarginTop: Int = 0
    private var notificationInactiveMarginTop: Int = 0
    private var notificationAnimationDuration: Long = 0

    constructor(context: Context) : super(context) {
        init(context, null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(context, attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(context, attrs)
    }

    override fun setSoundEffectsEnabled(soundEffectsEnabled: Boolean) {
        super.setSoundEffectsEnabled(soundEffectsEnabled)
        this.soundEffectsEnabled = soundEffectsEnabled
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        createItems()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (!isBehaviorTranslationSet) {
            //The translation behavior has to be set up after the super.onMeasure has been called.
            setBehaviorTranslationEnabled(behaviorTranslationEnabled)
            isBehaviorTranslationSet = true
        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val bundle = Bundle()
        bundle.putParcelable("superState", super.onSaveInstanceState())
        bundle.putInt("current_item", currentItem)
        bundle.putParcelableArrayList(
            "notifications",
            ArrayList<Notification>(notifications)
        )
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        var st: Parcelable? = state
        if (st is Bundle) {
            val bundle: Bundle = st
            currentItem = bundle.getInt("current_item")
            notifications = bundle.getParcelableArrayList("notifications")!!
            st = bundle.getParcelable("superState")
        }
        super.onRestoreInstanceState(st)
    }
    /////////////
    // PRIVATE //
    /////////////
    /**
     * Init
     *
     * @param context
     */
    private fun init(
        context: Context,
        attrs: AttributeSet?
    ) {

        // Item colors
        titleColorActive = ContextCompat.getColor(context, R.color.colorBottomNavigationAccent)
        titleColorInactive = ContextCompat.getColor(context, R.color.colorBottomNavigationInactive)
        itemDisableColor = ContextCompat.getColor(context, R.color.colorBottomNavigationDisable)

        // Colors for colored bottom navigation
        coloredTitleColorActive =
            ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored)
        coloredTitleColorInactive =
            ContextCompat.getColor(context, R.color.colorBottomNavigationInactiveColored)
        if (attrs != null) {
            val ta: TypedArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.AHBottomNavigationBehavior_Params!!,
                0,
                0
            )
            try {
                isTranslucentNavigationEnabled = ta.getBoolean(
                    R.styleable.AHBottomNavigationBehavior_Params_translucentNavigationEnabled,
                    false
                )
                titleColorActive = ta.getColor(
                    R.styleable.AHBottomNavigationBehavior_Params_accentColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationAccent)
                )
                titleColorInactive = ta.getColor(
                    R.styleable.AHBottomNavigationBehavior_Params_inactiveColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationInactive)
                )
                itemDisableColor = ta.getColor(
                    R.styleable.AHBottomNavigationBehavior_Params_disableColor,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationDisable)
                )
                coloredTitleColorActive = ta.getColor(
                    R.styleable.AHBottomNavigationBehavior_Params_coloredActive,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationActiveColored)
                )
                coloredTitleColorInactive = ta.getColor(
                    R.styleable.AHBottomNavigationBehavior_Params_coloredInactive,
                    ContextCompat.getColor(context, R.color.colorBottomNavigationInactiveColored)
                )
            } finally {
                ta.recycle()
            }
        }
        notificationTextColor = ContextCompat.getColor(context, android.R.color.white)
        bottomNavigationHeight = resources!!.getDimension(R.dimen.bottom_navigation_height).toInt()
        itemActiveColor = titleColorActive
        itemInactiveColor = titleColorInactive

        // Notifications
        notificationActiveMarginLeft =
            resources!!.getDimension(R.dimen.bottom_navigation_notification_margin_left_active)
                .toInt()
        notificationInactiveMarginLeft =
            resources!!.getDimension(R.dimen.bottom_navigation_notification_margin_left).toInt()
        notificationActiveMarginTop =
            resources!!.getDimension(R.dimen.bottom_navigation_notification_margin_top_active)
                .toInt()
        notificationInactiveMarginTop =
            resources!!.getDimension(R.dimen.bottom_navigation_notification_margin_top).toInt()
        notificationAnimationDuration = 150
        ViewCompat.setElevation(this, resources!!.getDimension(R.dimen.bottom_navigation_elevation))
        val params: ViewGroup.LayoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, bottomNavigationHeight
        )
        layoutParams = params
    }

    /**
     * Create the items in the bottom navigation
     */
    private fun createItems() {
        if (items.size < MIN_ITEMS) {
            Log.w(
                TAG,
                "The items list should have at least 3 items"
            )
        } else if (items.size > MAX_ITEMS) {
            Log.w(
                TAG,
                "The items list should not have more than 5 items"
            )
        }
        val layoutHeight: Int = resources!!.getDimension(R.dimen.bottom_navigation_height).toInt()
        removeAllViews()
        views.clear()
        backgroundColorView = View(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val backgroundLayoutParams = LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, calculateHeight(layoutHeight)
            )
            addView(backgroundColorView, backgroundLayoutParams)
            bottomNavigationHeight = layoutHeight
        }
        linearLayoutContainer = LinearLayout(context)
        linearLayoutContainer.orientation = LinearLayout.HORIZONTAL
        linearLayoutContainer.gravity = Gravity.CENTER
        val layoutParams = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, layoutHeight
        )
        addView(linearLayoutContainer, layoutParams)
        if (isClassic) {
            createClassicItems(linearLayoutContainer)
        } else {
            createSmallItems(linearLayoutContainer)
        }

        // Force a request layout after all the items have been created
        post { requestLayout() }
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private fun calculateHeight(layoutHeight: Int): Int {
        var layHeight: Int = layoutHeight
        if (!isTranslucentNavigationEnabled) return layHeight
        val resourceId: Int =
            resources.getIdentifier("navigation_bar_height", "dimen", "android")
        if (resourceId > 0) {
            navigationBarHeight = resources!!.getDimensionPixelSize(resourceId)
        }
        val attrs: IntArray =
            intArrayOf(android.R.attr.fitsSystemWindows, android.R.attr.windowTranslucentNavigation)
        val typedValue: TypedArray = context.theme.obtainStyledAttributes(attrs)
        val index = 1
        val translucentNavigation: Boolean = typedValue.getBoolean(index, true)
        if (hasImmersive() /*&& !fitWindow*/ && translucentNavigation) {
            layHeight += navigationBarHeight
        }
        typedValue.recycle()
        return layHeight
    }

    @SuppressLint("NewApi")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun hasImmersive(): Boolean {
        val d: Display =
            (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val realDisplayMetrics = DisplayMetrics()
        d.getRealMetrics(realDisplayMetrics)
        val realHeight: Int = realDisplayMetrics.heightPixels
        val realWidth: Int = realDisplayMetrics.widthPixels
        val displayMetrics = DisplayMetrics()
        d.getMetrics(displayMetrics)
        val displayHeight: Int = displayMetrics.heightPixels
        val displayWidth: Int = displayMetrics.widthPixels
        return (realWidth > displayWidth) || (realHeight > displayHeight)
    }
    // updated
    /**
     * Check if items must be classic
     *
     * @return true if classic (icon + title)
     */
    private val isClassic: Boolean
        get() = ((titleState != TitleState.ALWAYS_HIDE) && (
                titleState != TitleState.SHOW_WHEN_ACTIVE_FORCE) &&
                (items.size == MIN_ITEMS || titleState == TitleState.ALWAYS_SHOW))

    /**
     * Create classic items (only 3 items in the bottom navigation)
     *
     * @param linearLayout The layout where the items are added
     */
    private fun createClassicItems(linearLayout: LinearLayout) {
        val inflater: LayoutInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val height: Float = resources!!.getDimension(R.dimen.bottom_navigation_height)
        var minWidth: Float = resources!!.getDimension(R.dimen.bottom_navigation_min_width)
        var maxWidth: Float = resources!!.getDimension(R.dimen.bottom_navigation_max_width)
        if (titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            minWidth = resources!!.getDimension(R.dimen.bottom_navigation_small_inactive_min_width)
            maxWidth = resources!!.getDimension(R.dimen.bottom_navigation_small_inactive_max_width)
        }
        val layoutWidth: Int = width - paddingLeft - paddingRight
        if (layoutWidth == 0 || items.size == 0) {
            return
        }
        var itemWidth: Float = layoutWidth / items.size.toFloat()
        if (itemWidth < minWidth) {
            itemWidth = minWidth
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth
        }
        var activeSize: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_text_size_active)
        var inactiveSize: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_text_size_inactive)
        val activePaddingTop: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_margin_top_active).toInt()
        if (titleActiveTextSize != 0f && titleInactiveTextSize != 0f) {
            activeSize = titleActiveTextSize
            inactiveSize = titleInactiveTextSize
        } else if (titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            activeSize = resources!!.getDimension(R.dimen.bottom_navigation_text_size_forced_active)
            inactiveSize =
                resources!!.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive)
        }
        var iconDrawable: Drawable?
        for (i in items.indices) {
            val current: Boolean = currentItem == i
            val itemIndex: Int = i
            val item: BottomNavigationItem = items[itemIndex]
            val view: View =
                inflater.inflate(R.layout.bottom_navigation_item, this, false)
            val container: FrameLayout =
                view.findViewById<View>(R.id.bottom_navigation_container) as FrameLayout
            val icon: ImageView =
                view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
            val title: TextView =
                view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
            val notification: TextView =
                view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
            icon.setImageDrawable(item.getDrawable(context))
            title.text = item.getTitle(context!!)
            if (titleTypeface != null) {
                title.typeface = titleTypeface
            }
            if (titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
                container.setPadding(0, container.paddingTop, 0, container.paddingBottom)
            }
            if (current) {
                icon.isSelected = true
                // Update margins (icon & notification)
                if (view.layoutParams is MarginLayoutParams) {
                    val p: MarginLayoutParams = icon.layoutParams as MarginLayoutParams
                    p.setMargins(p.leftMargin, activePaddingTop, p.rightMargin, p.bottomMargin)
                    val paramsNotification: MarginLayoutParams =
                        notification.layoutParams as MarginLayoutParams
                    paramsNotification.setMargins(
                        notificationActiveMarginLeft, paramsNotification.topMargin,
                        paramsNotification.rightMargin, paramsNotification.bottomMargin
                    )
                    view.requestLayout()
                }
            } else {
                icon.isSelected = false
                val paramsNotification: MarginLayoutParams =
                    notification.layoutParams as MarginLayoutParams
                paramsNotification.setMargins(
                    notificationInactiveMarginLeft, paramsNotification.topMargin,
                    paramsNotification.rightMargin, paramsNotification.bottomMargin
                )
            }
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(defaultBackgroundColor)
            }
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, if (current) activeSize else inactiveSize)
            if (itemsEnabledStates[i]) {
                view.setOnClickListener { updateItems(itemIndex, true) }
                iconDrawable = Helper.getTintDrawable(
                    items[i].getDrawable(context)!!,
                    if (current) itemActiveColor else itemInactiveColor
                )
                if (selectedBackgroundVisible) {
                    container.setBackgroundColor(if (current) itemActiveColor else itemInactiveColor)
                } else {
                    icon.setImageDrawable(iconDrawable)
                    title.setTextColor(if (current) itemActiveColor else itemInactiveColor)
                }

                view.isSoundEffectsEnabled = soundEffectsEnabled
                view.isEnabled = true
            } else {
                iconDrawable = Helper.getTintDrawable(
                    items[i].getDrawable(context)!!,
                    itemDisableColor
                )

                if (selectedBackgroundVisible) {
                    container.setBackgroundColor(itemDisableColor)
                } else {
                    icon.setImageDrawable(iconDrawable)
                    title.setTextColor(itemDisableColor)
                }

                view.isClickable = true
                view.isEnabled = false
            }
            val params = LayoutParams(itemWidth.toInt(), height.toInt())
            linearLayout.addView(view, params)
            views.add(view)
        }
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Create small items (more than 3 items in the bottom navigation)
     *
     * @param linearLayout The layout where the items are added
     */
    private fun createSmallItems(linearLayout: LinearLayout) {
        val inflater: LayoutInflater =
            context!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val height: Float = resources!!.getDimension(R.dimen.bottom_navigation_height)
        val minWidth: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_small_inactive_min_width)
        val maxWidth: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_small_inactive_max_width)
        val layoutWidth: Int = width - paddingLeft - paddingRight
        if (layoutWidth == 0 || items.size == 0) {
            return
        }
        var itemWidth: Float = layoutWidth / items.size.toFloat()
        if (itemWidth < minWidth) {
            itemWidth = minWidth
        } else if (itemWidth > maxWidth) {
            itemWidth = maxWidth
        }
        val activeMarginTop: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_small_margin_top_active).toInt()
        val difference: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_small_selected_width_difference)
        selectedItemWidth = itemWidth + items.size * difference
        itemWidth -= difference
        notSelectedItemWidth = itemWidth
        var iconDrawable: Drawable?
        for (i in items.indices) {
            val itemIndex: Int = i
            val item: BottomNavigationItem = items[itemIndex]
            val view: View =
                inflater.inflate(R.layout.bottom_navigation_small_item, this, false)
            val icon: ImageView =
                view.findViewById<View>(R.id.bottom_navigation_small_item_icon) as ImageView
            val title: TextView =
                view.findViewById<View>(R.id.bottom_navigation_small_item_title) as TextView
            val notification: TextView =
                view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
            icon.setImageDrawable(item.getDrawable(context))
            if (titleState != TitleState.ALWAYS_HIDE) {
                title.text = item.getTitle(context!!)
            }
            if (titleActiveTextSize != 0f) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleActiveTextSize)
            }
            if (titleTypeface != null) {
                title.typeface = titleTypeface
            }
            if (i == currentItem) {
                icon.isSelected = true
                // Update margins (icon & notification)
                if (titleState != TitleState.ALWAYS_HIDE) {
                    if (view.layoutParams is MarginLayoutParams) {
                        val p: MarginLayoutParams = icon.layoutParams as MarginLayoutParams
                        p.setMargins(p.leftMargin, activeMarginTop, p.rightMargin, p.bottomMargin)
                        val paramsNotification: MarginLayoutParams =
                            notification.layoutParams as MarginLayoutParams
                        paramsNotification.setMargins(
                            notificationActiveMarginLeft, notificationActiveMarginTop,
                            paramsNotification.rightMargin, paramsNotification.bottomMargin
                        )
                        view.requestLayout()
                    }
                }
            } else {
                icon.isSelected = false
                val paramsNotification: MarginLayoutParams =
                    notification.layoutParams as MarginLayoutParams
                paramsNotification.setMargins(
                    notificationInactiveMarginLeft, notificationInactiveMarginTop,
                    paramsNotification.rightMargin, paramsNotification.bottomMargin
                )
            }
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(defaultBackgroundColor)
            }
            if (itemsEnabledStates[i]) {
                iconDrawable = Helper.getTintDrawable(
                    items[i].getDrawable(context)!!,
                    if (currentItem == i) itemActiveColor else itemInactiveColor
                )
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(if (currentItem == i) itemActiveColor else itemInactiveColor)
                title.alpha = (if (currentItem == i) 1F else 0F)
                view.setOnClickListener { updateSmallItems(itemIndex, true) }
                view.isSoundEffectsEnabled = soundEffectsEnabled
                view.isEnabled = true
            } else {
                iconDrawable = Helper.getTintDrawable(
                    items[i].getDrawable(context)!!,
                    itemDisableColor
                )
                icon.setImageDrawable(iconDrawable)
                title.setTextColor(itemDisableColor)
                title.alpha = 0f
                view.isClickable = true
                view.isEnabled = false
            }
            var width: Int = if (i == currentItem) selectedItemWidth.toInt() else itemWidth.toInt()
            if (titleState == TitleState.ALWAYS_HIDE) {
                width = (itemWidth * 1.16).toInt()
            }
            val params = LayoutParams(width, height.toInt())
            linearLayout.addView(view, params)
            views.add(view)
        }
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Update Items UI
     *
     * @param itemIndex   int: Selected item position
     * @param useCallback boolean: Use or not the callback
     */
    private fun updateItems(itemIndex: Int, useCallback: Boolean) {
        if (currentItem == itemIndex) {
            if (tabSelectedListener != null && useCallback) {
                tabSelectedListener!!.onTabSelected(itemIndex, true)
            }
            return
        }
        if (tabSelectedListener != null && useCallback) {
            val selectionAllowed: Boolean =
                tabSelectedListener!!.onTabSelected(itemIndex, false)
            if (!selectionAllowed) return
        }
        val activeMarginTop: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_margin_top_active).toInt()
        val inactiveMarginTop: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_margin_top_inactive).toInt()
        var activeSize: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_text_size_active)
        var inactiveSize: Float =
            resources!!.getDimension(R.dimen.bottom_navigation_text_size_inactive)
        if (titleActiveTextSize != 0f && titleInactiveTextSize != 0f) {
            activeSize = titleActiveTextSize
            inactiveSize = titleInactiveTextSize
        } else if (titleState == TitleState.ALWAYS_SHOW && items.size > MIN_ITEMS) {
            activeSize = resources!!.getDimension(R.dimen.bottom_navigation_text_size_forced_active)
            inactiveSize =
                resources!!.getDimension(R.dimen.bottom_navigation_text_size_forced_inactive)
        }
        for (i in views.indices) {
            val view: View = views[i]
            if (i == itemIndex) {
                val container: FrameLayout =
                    view.findViewById<View>(R.id.bottom_navigation_container) as FrameLayout
                val title: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon: ImageView =
                    view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
                val notification: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
                icon.isSelected = true
                Helper.updateTopMargin(icon, inactiveMarginTop, activeMarginTop)
                if (selectedBackgroundVisible)
                    Helper.updateBackColor(container, itemInactiveColor, itemActiveColor)
                else {
                    Helper.updateTextColor(title, itemInactiveColor, itemActiveColor)
                    Helper.updateDrawableColor(
                        items[itemIndex].getDrawable(context)!!, icon, itemInactiveColor,
                        itemActiveColor
                    )
                }

                Helper.updateTextSize(title, inactiveSize, activeSize)
                Helper.updateLeftMargin(
                    notification,
                    notificationInactiveMarginLeft,
                    notificationActiveMarginLeft
                )

                if (defaultBackgroundResource != 0) {
                    setBackgroundResource(defaultBackgroundResource)
                } else {
                    setBackgroundColor(defaultBackgroundColor)
                }
                backgroundColorView.setBackgroundColor(Color.TRANSPARENT)

            } else if (i == currentItem) {
                val container: FrameLayout =
                    view.findViewById<View>(R.id.bottom_navigation_container) as FrameLayout
                val title: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_item_title) as TextView
                val icon: ImageView =
                    view.findViewById<View>(R.id.bottom_navigation_item_icon) as ImageView
                val notification: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
                icon.isSelected = false
                Helper.updateTopMargin(icon, activeMarginTop, inactiveMarginTop)
                Helper.updateLeftMargin(
                    notification,
                    notificationActiveMarginLeft,
                    notificationInactiveMarginLeft
                )
                if (selectedBackgroundVisible)
                    Helper.updateBackColor(container, itemActiveColor, itemInactiveColor)
                else {
                    Helper.updateTextColor(title, itemActiveColor, itemInactiveColor)
                    Helper.updateDrawableColor(
                        items[currentItem].getDrawable(context)!!, icon, itemActiveColor,
                        itemInactiveColor
                    )
                }
                Helper.updateTextSize(title, activeSize, inactiveSize)
            }
        }
        currentItem = itemIndex
        if (currentItem > 0 && currentItem < items.size) {
            currentColor = items[currentItem].getColor(context)
        } else if (currentItem == CURRENT_ITEM_NONE) {
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(defaultBackgroundColor)
            }
            backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Update Small items UI
     *
     * @param itemIndex   int: Selected item position
     * @param useCallback boolean: Use or not the callback
     */
    private fun updateSmallItems(itemIndex: Int, useCallback: Boolean) {
        if (currentItem == itemIndex) {
            if (tabSelectedListener != null && useCallback) {
                tabSelectedListener!!.onTabSelected(itemIndex, true)
            }
            return
        }
        if (tabSelectedListener != null && useCallback) {
            val selectionAllowed: Boolean =
                tabSelectedListener!!.onTabSelected(itemIndex, false)
            if (!selectionAllowed) return
        }
        val activeMarginTop: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_small_margin_top_active).toInt()
        val inactiveMargin: Int =
            resources!!.getDimension(R.dimen.bottom_navigation_small_margin_top).toInt()
        for (i in views.indices) {
            val view: View = views[i]
            if (i == itemIndex) {
                val container: FrameLayout =
                    view.findViewById<View>(R.id.bottom_navigation_small_container) as FrameLayout
                val title: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_small_item_title) as TextView
                val icon: ImageView =
                    view.findViewById<View>(R.id.bottom_navigation_small_item_icon) as ImageView
                val notification: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
                icon.isSelected = true
                if (titleState != TitleState.ALWAYS_HIDE) {
                    Helper.updateTopMargin(icon, inactiveMargin, activeMarginTop)
                    Helper.updateLeftMargin(
                        notification,
                        notificationInactiveMarginLeft,
                        notificationActiveMarginLeft
                    )
                    Helper.updateTopMargin(
                        notification,
                        notificationInactiveMarginTop,
                        notificationActiveMarginTop
                    )
                    if (selectedBackgroundVisible)
                        Helper.updateBackColor(container, itemInactiveColor, itemActiveColor)
                    else
                        Helper.updateTextColor(title, itemInactiveColor, itemActiveColor)

                    Helper.updateWidth(container, notSelectedItemWidth, selectedItemWidth)
                }
                Helper.updateAlpha(title, 0f, 1f)
                Helper.updateDrawableColor(
                    items[itemIndex].getDrawable(context)!!, icon, itemInactiveColor,
                    itemActiveColor
                )

                if (defaultBackgroundResource != 0) {
                    setBackgroundResource(defaultBackgroundResource)
                } else {
                    setBackgroundColor(defaultBackgroundColor)
                }
                backgroundColorView.setBackgroundColor(Color.TRANSPARENT)

            } else if (i == currentItem) {
                val container: View =
                    view.findViewById(R.id.bottom_navigation_small_container)
                val title: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_small_item_title) as TextView
                val icon: ImageView =
                    view.findViewById<View>(R.id.bottom_navigation_small_item_icon) as ImageView
                val notification: TextView =
                    view.findViewById<View>(R.id.bottom_navigation_notification) as TextView
                icon.isSelected = false
                if (titleState != TitleState.ALWAYS_HIDE) {
                    Helper.updateTopMargin(icon, activeMarginTop, inactiveMargin)
                    Helper.updateLeftMargin(
                        notification,
                        notificationActiveMarginLeft,
                        notificationInactiveMarginLeft
                    )
                    Helper.updateTopMargin(
                        notification,
                        notificationActiveMarginTop,
                        notificationInactiveMarginTop
                    )
                    Helper.updateTextColor(title, itemActiveColor, itemInactiveColor)
                    Helper.updateWidth(container, selectedItemWidth, notSelectedItemWidth)
                }
                Helper.updateAlpha(title, 1f, 0f)
                Helper.updateDrawableColor(
                    items[currentItem].getDrawable(context)!!, icon, itemActiveColor,
                    itemInactiveColor
                )
            }
        }
        currentItem = itemIndex
        if (currentItem > 0 && currentItem < items.size) {
            currentColor = items[currentItem].getColor(context)
        } else if (currentItem == CURRENT_ITEM_NONE) {
            if (defaultBackgroundResource != 0) {
                setBackgroundResource(defaultBackgroundResource)
            } else {
                setBackgroundColor(defaultBackgroundColor)
            }
            backgroundColorView.setBackgroundColor(Color.TRANSPARENT)
        }
    }

    /**
     * Update notifications
     */
    private fun updateNotifications(updateStyle: Boolean, itemPosition: Int) {
        for (i in views.indices) {
            if (i >= notifications.size) {
                break
            }
            if (itemPosition != UPDATE_ALL_NOTIFICATIONS && itemPosition != i) {
                continue
            }
            val notificationItem: Notification? = notifications[i]
            val currentTextColor: Int =
                NotificationHelper.getTextColor(notificationItem!!, notificationTextColor)
            val currentBackgroundColor: Int = NotificationHelper.getBackgroundColor(
                notificationItem,
                notificationBackgroundColor
            )
            val notification: TextView = views[i]
                .findViewById<View>(R.id.bottom_navigation_notification) as TextView
            val currentValue: String = notification.text.toString()
            val animate: Boolean =
                currentValue != java.lang.String.valueOf(notificationItem.text)
            if (updateStyle) {
                notification.setTextColor(currentTextColor)
                if (notificationTypeface != null) {
                    notification.typeface = notificationTypeface
                } else {
                    notification.setTypeface(null, Typeface.BOLD)
                }
                if (notificationBackgroundDrawable != null) {
                    val drawable: Drawable =
                        notificationBackgroundDrawable!!.constantState!!.newDrawable()
                    notification.background = drawable
                } else if (currentBackgroundColor != 0) {
                    val defaultDrawable: Drawable? =
                        ContextCompat.getDrawable((context)!!, R.drawable.notification_background)
                    notification.background = Helper.getTintDrawable(
                        defaultDrawable!!,
                        currentBackgroundColor
                    )
                }
            }
            if (notificationItem.isEmpty && notification.text.isNotEmpty()) {
                notification.text = ""
                if (animate) {
                    notification.animate()
                        .scaleX(0f)
                        .scaleY(0f)
                        .alpha(0f)
                        .setInterpolator(AccelerateInterpolator())
                        .setDuration(notificationAnimationDuration)
                        .start()
                }
            } else if (!notificationItem.isEmpty) {
                notification.text = java.lang.String.valueOf(notificationItem.text)
                if (animate) {
                    notification.scaleX = 0f
                    notification.scaleY = 0f
                    notification.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .alpha(1f)
                        .setInterpolator(OvershootInterpolator())
                        .setDuration(notificationAnimationDuration)
                        .start()
                }
            }
        }
    }
    ////////////
    // PUBLIC //
    ////////////
    /**
     * Add an item at the given index
     */
    fun addItemAtIndex(index: Int, item: BottomNavigationItem) {
        if (items.size > MAX_ITEMS) {
            Log.w(
                TAG,
                "The items list should not have more than 5 items"
            )
        }
        if (index < items.size) {
            items.add(index, item)
        } else {
            Log.w(
                TAG,
                "The index is out of bounds (index: " + index + ", size: " + items.size + ")"
            )
        }
        createItems()
    }

    /**
     * Add an item
     */
    fun addItem(item: BottomNavigationItem) {
        if (items.size > MAX_ITEMS) {
            Log.w(
                TAG,
                "The items list should not have more than 5 items"
            )
        }
        items.add(item)
        createItems()
    }

    /**
     * Add all items
     */
    fun addItems(items: List<BottomNavigationItem>) {
        if (items.size > MAX_ITEMS || (this.items.size + items.size) > MAX_ITEMS) {
            Log.w(
                TAG,
                "The items list should not have more than 5 items"
            )
        }
        this.items.addAll(items)
        createItems()
    }

    /**
     * Remove an item at the given index
     */
    fun removeItemAtIndex(index: Int) {
        if (index < items.size) {
            items.removeAt(index)
            createItems()
        }
    }

    /**
     * Remove all items
     */
    fun removeAllItems() {
        items.clear()
        createItems()
    }

    /**
     * Refresh the AHBottomView
     */
    fun refresh() {
        createItems()
    }

    /**
     * Return the number of items
     *
     * @return int
     */
    val itemsCount: Int
        get() {
            return items.size
        }

    /**
     * Return the bottom navigation background color
     *
     * @return The bottom navigation background color
     */
    fun getDefaultBackgroundColor(): Int {
        return defaultBackgroundColor
    }

    /**
     * Set the bottom navigation background color
     *
     * @param defaultBackgroundColor The bottom navigation background color
     */
    fun setDefaultBackgroundColor(@ColorInt defaultBackgroundColor: Int) {
        this.defaultBackgroundColor = defaultBackgroundColor
        createItems()
    }

    /**
     * Set the bottom navigation background resource
     *
     * @param defaultBackgroundResource The bottom navigation background resource
     */
    fun setDefaultBackgroundResource(@DrawableRes defaultBackgroundResource: Int) {
        this.defaultBackgroundResource = defaultBackgroundResource
        createItems()
    }

    /**
     * Get the accent color (used when the view contains 3 items)
     *
     * @return The default accent color
     */
    /**
     * Set the accent color (used when the view contains 3 items)
     *
     * @param activeColor The new accent color
     */
    fun setActiveColor(@ColorInt activeColor: Int) {
        titleColorActive = activeColor
        itemActiveColor = activeColor
        createItems()
    }

    /**
     * Get the inactive color (used when the view contains 3 items)
     *
     * @return The inactive color
     */
    /**
     * Set the inactive color (used when the view contains 3 items)
     *
     * @param inactiveColor The inactive color
     */

    fun setInActiveColor(@ColorInt inactiveColor: Int) {
        titleColorInactive = inactiveColor
        itemInactiveColor = inactiveColor
        createItems()
    }

    /**
     * Set selected background visibility
     */
    fun setSelectedBackgroundVisible(visible: Boolean) {
        selectedBackgroundVisible = visible
        createItems()
    }

    /**
     * Set notification typeface
     *
     * @param typeface Typeface
     */
    fun setTitleTypeface(typeface: Typeface?) {
        titleTypeface = typeface
        createItems()
    }

    /**
     * Set title text size in pixels
     *
     * @param activeSize
     * @param inactiveSize
     */
    fun setTitleTextSize(activeSize: Float, inactiveSize: Float) {
        titleActiveTextSize = activeSize
        titleInactiveTextSize = inactiveSize
        createItems()
    }

    /**
     * Set title text size in SP
     *
     * +	 * @param activeSize in sp
     * +	 * @param inactiveSize in sp
     */
    fun setTitleTextSizeInSp(activeSize: Float, inactiveSize: Float) {
        titleActiveTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            activeSize,
            resources!!.displayMetrics
        )
        titleInactiveTextSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            inactiveSize,
            resources!!.displayMetrics
        )
        createItems()
    }

    /**
     * Get item at the given index
     *
     * @param position int: item position
     * @return The item at the given position
     */
    fun getItem(position: Int): BottomNavigationItem? {
        if (position < 0 || position > items.size - 1) {
            Log.w(
                TAG,
                "The position is out of bounds of the items (" + items.size + " elements)"
            )
            return null
        }
        return items.get(position)
    }

    /**
     * Get the current item
     *
     * @return The current item position
     */
    fun getCurrentItem(): Int {
        return currentItem
    }

    /**
     * Set the current item
     *
     * @param position int: position
     */
    fun setCurrentItem(position: Int) {
        setCurrentItem(position, true)
    }

    /**
     * Set the current item
     *
     * @param position    int: item position
     * @param useCallback boolean: use or not the callback
     */
    private fun setCurrentItem(position: Int, useCallback: Boolean) {
        if (position >= items.size) {
            Log.w(
                TAG,
                "The position is out of bounds of the items (" + items.size + " elements)"
            )
            return
        }
        if (((titleState != TitleState.ALWAYS_HIDE) && (
                    titleState != TitleState.SHOW_WHEN_ACTIVE_FORCE) &&
                    (items.size == MIN_ITEMS || titleState == TitleState.ALWAYS_SHOW))
        ) {
            updateItems(position, useCallback)
        } else {
            updateSmallItems(position, useCallback)
        }
    }

    /**
     * Return if the behavior translation is enabled
     *
     * @return a boolean value
     */
    fun isBehaviorTranslationEnabled(): Boolean {
        return behaviorTranslationEnabled
    }

    /**
     * Set the behavior translation value
     *
     * @param behaviorTranslationEnabled boolean for the state
     */
    private fun setBehaviorTranslationEnabled(behaviorTranslationEnabled: Boolean) {
        this.behaviorTranslationEnabled = behaviorTranslationEnabled
        if (parent is CoordinatorLayout) {
            val params: ViewGroup.LayoutParams = layoutParams
            bottomNavigationBehavior.setBehaviorTranslationEnabled(
                behaviorTranslationEnabled,
                navigationBarHeight
            )
            if (navigationPositionListener != null) {
                bottomNavigationBehavior.setOnNavigationPositionListener(navigationPositionListener)
            }
            (params as CoordinatorLayout.LayoutParams).behavior = bottomNavigationBehavior
            if (needHideBottomNavigation) {
                needHideBottomNavigation = false
                bottomNavigationBehavior.hideView(
                    this,
                    bottomNavigationHeight,
                    hideBottomNavigationWithAnimation
                )
            }
        }
    }

    /**
     * Manage the floating action button behavior with AHBottomNavigation
     * @param fab Floating Action Button
     */
    fun manageFloatingActionButtonBehavior(fab: FloatingActionButton) {
        if (fab.parent is CoordinatorLayout) {
            val fabBehavior = BottomNavigationFABBehavior(navigationBarHeight)
            (fab.layoutParams as CoordinatorLayout.LayoutParams).behavior = fabBehavior
        }
    }

    @JvmOverloads
    fun hideBottomNavigation(withAnimation: Boolean = true) {
        bottomNavigationBehavior.hideView(this, bottomNavigationHeight, withAnimation)
    }

    @JvmOverloads
    fun restoreBottomNavigation(withAnimation: Boolean = true) {
        bottomNavigationBehavior.resetOffset(this, withAnimation)
    }

    /**
     * Return the title state for display
     *
     * @return TitleState
     */
    fun getTitleState(): TitleState {
        return titleState
    }

    /**
     * Sets the title state for each tab
     * SHOW_WHEN_ACTIVE: when a tab is focused
     * ALWAYS_SHOW: show regardless of which tab is in focus
     * ALWAYS_HIDE: never show tab titles
     * Note: Always showing the title is against Material Design guidelines
     *
     * @param titleState TitleState
     */
    fun setTitleState(titleState: TitleState) {
        this.titleState = titleState
        createItems()
    }

    /**
     * Set AHOnTabSelectedListener
     */
    fun setOnTabSelectedListener(tabSelectedListener: OnTabSelectedListener?) {
        this.tabSelectedListener = tabSelectedListener!!
    }

    /**
     * Remove AHOnTabSelectedListener
     */
    fun removeOnTabSelectedListener() {
        tabSelectedListener = null
    }

    /**
     * Set OnNavigationPositionListener
     */
    fun setOnNavigationPositionListener(navigationPositionListener: OnNavigationPositionListener?) {
        this.navigationPositionListener = navigationPositionListener!!
        bottomNavigationBehavior.setOnNavigationPositionListener(navigationPositionListener)
    }

    /**
     * Remove OnNavigationPositionListener()
     */
    fun removeOnNavigationPositionListener() {
        navigationPositionListener = null
        bottomNavigationBehavior.removeOnNavigationPositionListener()
    }

    /**
     * Set the notification number
     *
     * @param nbNotification int
     * @param itemPosition   int
     */
    fun setNotification(nbNotification: Int, itemPosition: Int) {
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(
                String.format(
                    Locale.US,
                    EXCEPTION_INDEX_OUT_OF_BOUNDS,
                    itemPosition,
                    items.size
                )
            )
        }
        val title: String = if (nbNotification == 0) "" else nbNotification.toString()
        notifications[itemPosition] = Notification.justText(title)
        updateNotifications(false, itemPosition)
    }

    /**
     * Set notification text
     *
     * @param title        String
     * @param itemPosition int
     */
    fun setNotification(title: String?, itemPosition: Int) {
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(
                String.format(
                    Locale.US,
                    EXCEPTION_INDEX_OUT_OF_BOUNDS,
                    itemPosition,
                    items.size
                )
            )
        }
        notifications.set(itemPosition, Notification.justText(title))
        updateNotifications(false, itemPosition)
    }

    /**
     * Set fully customized Notification
     *
     * @param notification AHNotification
     * @param itemPosition int
     */
    fun setNotification(notification: Notification?, itemPosition: Int) {
        var notify: Notification? = notification
        if (itemPosition < 0 || itemPosition > items.size - 1) {
            throw IndexOutOfBoundsException(
                String.format(
                    Locale.US,
                    EXCEPTION_INDEX_OUT_OF_BOUNDS,
                    itemPosition,
                    items.size
                )
            )
        }
        if (notify == null) {
            notify = Notification() // instead of null, use empty notification
        }
        notifications[itemPosition] = notify
        updateNotifications(true, itemPosition)
    }

    /**
     * Set notification text color
     *
     * @param textColor int
     */
    fun setNotificationTextColor(@ColorInt textColor: Int) {
        notificationTextColor = textColor
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification text color
     *
     * @param textColor int
     */
    fun setNotificationTextColorResource(@ColorRes textColor: Int) {
        notificationTextColor = ContextCompat.getColor((context)!!, textColor)
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background resource
     *
     * @param drawable Drawable
     */
    fun setNotificationBackground(drawable: Drawable?) {
        notificationBackgroundDrawable = drawable
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background color
     *
     * @param color int
     */
    fun setNotificationBackgroundColor(@ColorInt color: Int) {
        notificationBackgroundColor = color
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification background color
     *
     * @param color int
     */
    fun setNotificationBackgroundColorResource(@ColorRes color: Int) {
        notificationBackgroundColor = ContextCompat.getColor((context)!!, color)
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set notification typeface
     *
     * @param typeface Typeface
     */
    fun setNotificationTypeface(typeface: Typeface?) {
        notificationTypeface = typeface
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    fun setNotificationAnimationDuration(notificationAnimationDuration: Long) {
        this.notificationAnimationDuration = notificationAnimationDuration
        updateNotifications(true, UPDATE_ALL_NOTIFICATIONS)
    }

    /**
     * Set the notification margin left
     *
     * @param activeMargin
     * @param inactiveMargin
     */
    fun setNotificationMarginLeft(activeMargin: Int, inactiveMargin: Int) {
        notificationActiveMarginLeft = activeMargin
        notificationInactiveMarginLeft = inactiveMargin
        createItems()
    }

    /**
     * Activate or not the elevation
     *
     * @param useElevation boolean
     */
    fun setUseElevation(useElevation: Boolean) {
        ViewCompat.setElevation(
            this,
            (if (useElevation) resources!!.getDimension(R.dimen.bottom_navigation_elevation) else 0F)
        )
        clipToPadding = false
    }

    /**
     * Activate or not the elevation, and set the value
     *
     * @param useElevation boolean
     * @param elevation    float
     */
    fun setElevation(useElevation: Boolean, elevation: Float) {
        ViewCompat.setElevation(this, (if (useElevation) elevation else 0F))
        clipToPadding = false
    }

    /**
     * Return if the Bottom Navigation is hidden or not
     */
    val isHidden: Boolean
        get() {
            return bottomNavigationBehavior.isHidden
        }

    /**
     * Get the view at the given position
     * @param position int
     * @return The view at the position, or null
     */
    fun getViewAtPosition(position: Int): View? {
        if ((position >= 0) && (position < linearLayoutContainer.childCount)
        ) {
            return linearLayoutContainer.getChildAt(position)
        }
        return null
    }

    /**
     * Enable the tab item at the given position
     * @param position int
     */
    fun enableItemAtPosition(position: Int) {
        if (position < 0 || position > items.size - 1) {
            Log.w(
                TAG,
                "The position is out of bounds of the items (" + items.size + " elements)"
            )
            return
        }
        itemsEnabledStates[position]
        createItems()
    }

    /**
     * Disable the tab item at the given position
     * @param position int
     */
    fun disableItemAtPosition(position: Int) {
        if (position < 0 || position > items.size - 1) {
            Log.w(
                TAG,
                "The position is out of bounds of the items (" + items.size + " elements)"
            )
            return
        }
        itemsEnabledStates[position]
        createItems()
    }

    /**
     * Set the item disable color
     * @param itemDisableColor int
     */
    fun setItemDisableColor(@ColorInt itemDisableColor: Int) {
        this.itemDisableColor = itemDisableColor
    }
    ////////////////
    // INTERFACES //
    ////////////////
    /**
     *
     */
    interface OnTabSelectedListener {
        /**
         * Called when a tab has been selected (clicked)
         *
         * @param position    int: Position of the selected tab
         * @param wasSelected boolean: true if the tab was already selected
         * @return boolean: true for updating the tab UI, false otherwise
         */
        fun onTabSelected(position: Int, wasSelected: Boolean): Boolean
    }

    interface OnNavigationPositionListener {
        /**
         * Called when the bottom navigation position is changed
         *
         * @param y int: y translation of bottom navigation
         */
        fun onPositionChange(y: Int)
    }

    companion object {
        // Constant
        const val CURRENT_ITEM_NONE: Int = -1
        const val UPDATE_ALL_NOTIFICATIONS: Int = -1

        // Static
        private const val TAG: String = "AHBottomNavigation"
        private const val EXCEPTION_INDEX_OUT_OF_BOUNDS: String =
            "The position (%d) is out of bounds of the items (%d elements)"
        private const val MIN_ITEMS: Int = 3
        private const val MAX_ITEMS: Int = 5

    }
}