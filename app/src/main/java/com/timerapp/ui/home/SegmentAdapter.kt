package com.timerapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timerapp.databinding.ItemSegmentBinding
import com.timerapp.model.Segment

class SegmentAdapter(
    private var segments: List<Segment>,
    private val onItemClick: (segment: Segment, position: Int) -> Unit
) : RecyclerView.Adapter<SegmentAdapter.SegmentViewHolder>() {

    class SegmentViewHolder(
        private val binding: ItemSegmentBinding,
        private val onItemClick: (segment: Segment, position: Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        
        fun bind(segment: Segment, position: Int) {
            binding.textSegmentName.text = segment.name
            
            // Set click listener for the entire item
            binding.root.setOnClickListener {
                onItemClick(segment, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentViewHolder {
        val binding = ItemSegmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SegmentViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: SegmentViewHolder, position: Int) {
        holder.bind(segments[position], position)
    }

    override fun getItemCount(): Int = segments.size

    fun updateSegments(newSegments: List<Segment>) {
        segments = newSegments
        notifyDataSetChanged()
    }
}