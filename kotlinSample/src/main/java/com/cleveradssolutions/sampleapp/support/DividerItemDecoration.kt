package com.cleveradssolutions.sampleapp.support

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cleveradssolutions.sampleapp.R

class DividerItemDecoration(context: Context) : RecyclerView.ItemDecoration() {

    private val divider: Drawable = ContextCompat.getDrawable(context, R.drawable.divider)!!

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight

        val lastIndex = parent.childCount - 1
        if (lastIndex > 0) {
            for (i in 0 until lastIndex) {
                val child = parent.getChildAt(i)
                val params = child.layoutParams as RecyclerView.LayoutParams
                val top = child.bottom + params.bottomMargin
                val bottom = top + divider.intrinsicHeight

                divider.setBounds(left, top, right, bottom)
                divider.draw(c)
            }
        }
    }
}
