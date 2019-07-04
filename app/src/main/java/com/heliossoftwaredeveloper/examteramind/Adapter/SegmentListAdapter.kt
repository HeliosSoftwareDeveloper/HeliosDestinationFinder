/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.examteramind.Adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.directions.route.Segment
import com.heliossoftwaredeveloper.examteramind.Adapter.ViewHolder.SegmentViewHolder

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * Adapter class for SegmentList
 */

class SegmentListAdapter : RecyclerView.Adapter<SegmentViewHolder>() {

    private var cachedSegmentList : List<Segment> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentViewHolder {
        return SegmentViewHolder(LayoutInflater.from(parent.context), parent)
    }

    override fun getItemCount(): Int {
        return cachedSegmentList.size
    }

    override fun onBindViewHolder(holder: SegmentViewHolder, position: Int) {
        holder.bind(cachedSegmentList.get(position))
    }

    /**
     * Method to update the adapter list
     *
     * @param listSegment listItems to add
     * */
    fun updateList(listSegment: List<Segment>) {
        cachedSegmentList = listSegment
        notifyDataSetChanged()
    }
}
