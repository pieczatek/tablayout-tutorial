package com.bob.tabs

import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Typeface
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.custom_tab.view.*
import android.support.design.widget.TabLayout

class SlideLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0) : TabLayout(context, attrs, defStyleAttr) {


    private val MAX_ZOOM = 1.0f
    private val MIN_ZOOM = 0.6f
    private val SCROLL_TIME: Long = 600
    private val NEW_CHECK_TIME: Long = 100
    private val MIN_DISTANCE_PER_CHECK_TIME = 50 // TODO: from px to dp

    private var scrollerTask: Runnable? = null
    private var initialPosition = 0
    private var mPaddingLeft = 0
    private var screenPos = IntArray(2)

    private val scrollAnimatorListener = object : Animator.AnimatorListener {

        override fun onAnimationStart(animation: Animator) = Unit

        override fun onAnimationEnd(animation: Animator) {
            getMiddleTab()?.select()
        }

        override fun onAnimationCancel(animation: Animator) {
            getMiddleTab()?.select()
        }

        override fun onAnimationRepeat(animation: Animator) = Unit
    }

    init {
        addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {

            override fun onTabSelected(tab: TabLayout.Tab) {
                tab.customView?.tabText?.setTypeface(null, Typeface.BOLD)
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {
                tab.customView?.tabText?.setTypeface(null, Typeface.NORMAL)
            }

            override fun onTabReselected(tab: TabLayout.Tab) = Unit

        })

        scrollerTask = Runnable {

            val newPosition = scrollX
            val distance = initialPosition - newPosition

            if (distance <= MIN_DISTANCE_PER_CHECK_TIME) {
                stopScrolling()
                onScrollStopped()
            } else {
                initialPosition = scrollX
                postDelayed(scrollerTask, NEW_CHECK_TIME)
            }
        }

        setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                startScrollerTask()
            }

            false
        }
    }

    private fun onScrollStopped() {
        val tab = getMiddleTab()
        val tabPosition = tab?.position ?: 0

        getTabView(tabPosition)?.let {
            val scrollX = (it.x + it.width / 2 - width / 2).toInt()
            val animator = ObjectAnimator.ofInt(this@SlideLayout, "scrollX", scrollX)
                    .setDuration(SCROLL_TIME)

            animator.addListener(scrollAnimatorListener)
            animator.start()
        }
    }

    override fun onDraw(canvas: Canvas) {

        getTabContainer()?.let {
            for (i in 0 until it.childCount) {
                getTabView(i)?.let {
                    scaleTabView(it)
                }
            }
        }

        super.onDraw(canvas)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)

        val lastTabIndex = getTabContainer()?.childCount?.minus(1) ?: 0
        val firstTab = getTabView(0)
        val lastTab = getTabView(lastTabIndex)

        if (firstTab != null && lastTab != null) {
            mPaddingLeft = width / 2 - firstTab.width / 2
            val paddingRight: Int = width / 2 - lastTab.width / 2

            ViewCompat.setPaddingRelative(getTabContainer(), mPaddingLeft, 0, paddingRight, 0)
        }
    }


    fun addTab(text: String, resourceId: Int) {
        val tab = LayoutInflater.from(context).inflate(R.layout.custom_tab, null)
        tab.tabImage.setImageResource(resourceId)
        tab.tabText.text = text

        addTab(newTab().setCustomView(tab))
    }


    private fun getMiddleTab(): TabLayout.Tab? {

        var position = 0
        val tabCount = getTabContainer()?.childCount ?: 0
        val centerX = scrollX + width / 2
        var tabEndX = mPaddingLeft

        for (i in 0 until tabCount) {
            getTabView(i)?.let {
                tabEndX += it.width
            }

            if (centerX <= tabEndX) {
                position = i
                break
            }
        }

        return getTabAt(position)
    }

    private fun getTabContainer(): ViewGroup? = getChildAt(0) as? ViewGroup

    private fun getTabView(position: Int): View? = getTabContainer()?.getChildAt(position)

    private fun stopScrolling() = smoothScrollBy(0, 0)

    private fun scaleTabView(v: View) {

        v.getLocationOnScreen(screenPos)
        val pos = screenPos[0]
        val width = v.width

        val scale: Float
        val tabCenter = pos + width / 2

        if (tabCenter <= 0 || getWidth() <= tabCenter) {
            scale = MIN_ZOOM
        } else {
            val sliderCenter = (getWidth() / 2).toFloat()
            val distance = Math.abs(sliderCenter - tabCenter)
            scale = MAX_ZOOM - (MAX_ZOOM - MIN_ZOOM) * distance / sliderCenter
        }

        /* View draw start in left top corner */
        v.pivotY = 0f

        v.scaleX = scale
        v.scaleY = scale
    }

    private fun startScrollerTask() {
        initialPosition = scrollX
        postDelayed(scrollerTask, NEW_CHECK_TIME)
    }

}