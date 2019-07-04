/* (c) Helios Software Developer. All rights reserved. */
package com.heliossoftwaredeveloper.examteramind.Adapter.ViewHolder

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.directions.route.Segment
import com.heliossoftwaredeveloper.examteramind.R

/**
 * Created by Ruel N. Grajo on 07/02/2019.
 *
 * ViewHolder class for Segment ViewHolder
 */

class SegmentViewHolder(inflater: LayoutInflater, parent: ViewGroup) : RecyclerView.ViewHolder(inflater.inflate(R.layout.item_segment, parent, false)) {

    private var txtDistance : TextView = itemView.findViewById(R.id.txtDistance)
    private var txtInstructions : TextView = itemView.findViewById(R.id.txtInstructions)

    fun bind(segment: Segment) {
        segment.startPoint()
        txtDistance.text = segment.instruction
        txtInstructions.text = String.format("Distance: %1s m", segment.distance * 1000)
    }
}