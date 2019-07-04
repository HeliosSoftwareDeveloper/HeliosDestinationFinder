/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.examteramind.Adapter.Decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * Created by Ruel N. Grajo on 06/06/2019.
 *
 * ItemDecoration class for Divider Space per item
 */

class DividerSpaceItemDecoration(private val spaceSize: Int, isHorizontal : Boolean) : RecyclerView.ItemDecoration() {

    private val mIsHorizontal = isHorizontal

    override fun getItemOffsets(outRect: Rect, view: View?, parent: RecyclerView, state: RecyclerView.State?) {
        super.getItemOffsets(outRect, view, parent, state)
        if (parent.getChildAdapterPosition(view) != parent.adapter.itemCount - 1) {
            if (mIsHorizontal) {
                outRect.right = spaceSize
            } else {
                outRect.bottom = spaceSize
            }
        }
    }
}