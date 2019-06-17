package com.zero.tzz.pageindexview

import android.content.Context
import android.content.res.Resources
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import java.util.*

/**
 *
 * @author lucy
 * @date 2019-06-17 14:37
 * @description //TODO
 */
class PageIndexView : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr)

    companion object {
        /**
         * 初始显示最大页数
         */
        private const val LIMIT_PAGE = 8
        /**
         * 省略个数 - 翻页一次3个
         */
        private const val ELLIPSIS_PAGE_COUNT = 3
        /**
         * RecyclerView每页加载Item个数
         */
        private const val ITEM_PAGE_COUNT = 6
    }

    /**
     * 向前重新布局页面索引
     */
    private var mPreNeedUpdatePage = -1
    /**
     * 向后重新布局页面索引
     */
    private var mNextNeedUpdatePage = LIMIT_PAGE - 1
    private lateinit var mLinearLayoutManager: LinearLayoutManager
    private var mLastPage: Int = 0
    private var mPageCount: Int = 0
    private var mItemPageCount: Int = ITEM_PAGE_COUNT
    private var mEllipsisPageCount: Int = ELLIPSIS_PAGE_COUNT
    private var mLimitPage: Int = LIMIT_PAGE
    private val mPages = ArrayList<TextView>()

    private fun doNext() {
        val firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
        mLinearLayoutManager.scrollToPositionWithOffset(firstVisibleItemPosition + mItemPageCount, 0)
    }

    private fun doPrevious() {
        val firstVisibleItemPosition = mLinearLayoutManager.findFirstVisibleItemPosition()
        mLinearLayoutManager.scrollToPositionWithOffset(if (firstVisibleItemPosition - mItemPageCount < 0) 0 else firstVisibleItemPosition - mItemPageCount, 0)
    }

    private fun setCurrentPage(page: Int) {
        if (page < 0 || page >= mPages.size) {
            return
        }
        val last = mPages[mLastPage]
        last.isSelected = false
        val current = mPages[page]
        current.isSelected = true
        updatePageInfo(page)
        mLastPage = page
    }

    private fun calculatePage(size: Int) {
        val page = size / mItemPageCount
        val i = if (size % mItemPageCount == 0) 0 else 1
        mPageCount = page + i
        mPreNeedUpdatePage = -1
        mNextNeedUpdatePage = mLimitPage - 1
        setPageCount(mPageCount)
    }

    private fun setPageCount(pageCount: Int) {
        if (pageCount <= 0) {
            removeAllViews()
            mPages.clear()
            return
        }
        removeAllViews()
        mPages.clear()
        addPage("<", true, false)
        for (i in 0 until pageCount) {
            if (i < mLimitPage) {
                addPage((i + 1).toString(), true, true)
            } else if (i == pageCount - 1) {
                addPage("...", false, false)
                addPage((i + 1).toString(), true, true)
            } else {
                addPage((i + 1).toString())
            }
        }
        addPage(">", true, false)
        mLastPage = 0
        setCurrentPage(mLastPage)
    }

    private fun updatePageInfo(clickPage: Int) {
        // 不够页 当前页 不操作
        if (mPages.size <= mLimitPage || mLastPage == clickPage) {
            return
        }
        var drawFlag = false
        // 第一页
        if (clickPage == 0) {
            removeAllViews()
            mPreNeedUpdatePage = 1
            mNextNeedUpdatePage = mLimitPage - 1
            drawFlag = true
        } else if (clickPage == mPages.size - 1) {
            removeAllViews()
            // 展示limit个页数
            mPreNeedUpdatePage = clickPage - mLimitPage + 1
            mNextNeedUpdatePage = clickPage
            drawFlag = true
        } else if (clickPage == mNextNeedUpdatePage) {
            removeAllViews()
            if (mPreNeedUpdatePage == -1) {
                mPreNeedUpdatePage += mEllipsisPageCount + 2
            } else {
                mPreNeedUpdatePage += mEllipsisPageCount
            }
            if (mNextNeedUpdatePage + mEllipsisPageCount > mPages.size - 1) {
                mNextNeedUpdatePage = mPages.size - 1
                mPreNeedUpdatePage = mPages.size - mLimitPage
            } else {
                mNextNeedUpdatePage += mEllipsisPageCount
            }
            drawFlag = true
        } else if (clickPage == mPreNeedUpdatePage) {
            removeAllViews()
            if (mPreNeedUpdatePage - mEllipsisPageCount < 0) {
                mPreNeedUpdatePage = 1
                mNextNeedUpdatePage = mLimitPage - 1
            } else {
                mPreNeedUpdatePage -= mEllipsisPageCount
                if (mNextNeedUpdatePage == mPages.size - 1) {
                    mNextNeedUpdatePage = mNextNeedUpdatePage - mEllipsisPageCount - 1
                } else {
                    mNextNeedUpdatePage -= mEllipsisPageCount
                }
            }
            drawFlag = true
        }
        // 绘制页码
        if (drawFlag) {
            addPage("<", true, false)
            for (i in mPages.indices) {
                val textView = mPages.get(i)
                if (i == 0) {
                    addView(textView)
                    if (mPreNeedUpdatePage != 1) {
                        addPage("...", false, false)
                    } else {
                        mPreNeedUpdatePage = -1
                    }
                } else {
                    if (mPreNeedUpdatePage <= 0) {
                        if (i <= mPreNeedUpdatePage) {
                            addView(textView)
                        }
                    }
                    if (i >= mPreNeedUpdatePage) {
                        if (mNextNeedUpdatePage > mPages.size - 1) {
                            addView(textView)
                        } else {
                            if (i <= mNextNeedUpdatePage) {
                                addView(textView)
                            }
                            if (i == mPages.size - 1 && mNextNeedUpdatePage != i) {
                                addPage("...", false, false)
                                addView(textView)
                            }
                        }
                    }
                }
            }
            addPage(">", true, false)
            if (mNextNeedUpdatePage > mPages.size - 1) {
                mNextNeedUpdatePage = mPages.size - 1
                mPreNeedUpdatePage = mPages.size - 1 - mLimitPage + 1
            }
        }
    }

    /**
     * @param text 内容
     * @param background 是否添加背景
     * @param addPage 是否添加为页码
     */
    private fun addPage(text: String, background: Boolean, addPage: Boolean) {
        val textSize = sp2px(3f)
        val width = dp2px(20f)
        val height = dp2px(20f)
        val margin = dp2px(3f)
        val textView = TextView(context)
        textView.setTextColor(ContextCompat.getColorStateList(context, R.color.selector_page_index_color))
        textView.textSize = textSize.toFloat()
        textView.gravity = Gravity.CENTER
        textView.text = text
        if (background) {
            textView.setBackgroundResource(R.drawable.selector_page_index)
        }
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.leftMargin = margin
        textView.layoutParams = layoutParams
        if (addPage) {
            mPages.add(textView)
            registPageItemClickListener(textView)
        } else {
            registLeftAndRightClickListener(text, textView)
        }
        addView(textView)
    }

    private fun registPageItemClickListener(textView: TextView) {
        textView.setOnClickListener { v ->
            val view = v as TextView
            val string = view.text.toString()
            val integer = Integer.valueOf(string)
            val i = (integer!! - 1) * mItemPageCount
            setCurrentPage(integer - 1)
            mLinearLayoutManager.scrollToPositionWithOffset(i, 0)
        }
    }

    private fun registLeftAndRightClickListener(text: String, textView: TextView) {
        if (text.equals(">", ignoreCase = true)) {
            textView.setOnClickListener {
                val integer = Integer.valueOf(mPages[mLastPage].text.toString())
                setCurrentPage(integer!!)
                doNext()
            }
        } else if (text.equals("<", ignoreCase = true)) {
            textView.setOnClickListener {
                val integer = Integer.valueOf(mPages[mLastPage].text.toString())
                setCurrentPage(integer!! - 2)
                doPrevious()
            }
        }
    }

    private fun addPage(text: String) {
        val textSize = sp2px(3f)
        val width = dp2px(20f)
        val height = dp2px(20f)
        val margin = dp2px(3f)
        val textView = TextView(context)
        textView.setTextColor(ContextCompat.getColorStateList(context, R.color.selector_page_index_color))
        textView.textSize = textSize.toFloat()
        textView.gravity = Gravity.CENTER
        textView.text = text
        textView.setBackgroundResource(R.drawable.selector_page_index)
        val layoutParams = LinearLayout.LayoutParams(width, height)
        layoutParams.leftMargin = margin
        textView.layoutParams = layoutParams
        mPages.add(textView)
        registPageItemClickListener(textView)
    }

    fun dp2px(dpValue: Float): Int = (dpValue * Resources.getSystem().displayMetrics.density + 0.5f).toInt()

    fun sp2px(dpValue: Float): Int = (dpValue * Resources.getSystem().displayMetrics.scaledDensity + 0.5f).toInt()

    /************************************************** 外部调用 **************************************************/
    fun show(recyclerView: RecyclerView,
             size: Int = 0,
             pageCount: Int = ITEM_PAGE_COUNT,
             ellipsisPageCount: Int = ELLIPSIS_PAGE_COUNT,
             limitPage: Int = LIMIT_PAGE) {
        mEllipsisPageCount = ellipsisPageCount
        mLimitPage = limitPage
        mPageCount = pageCount
        mLinearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
        calculatePage(size)
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0)
    }
}
