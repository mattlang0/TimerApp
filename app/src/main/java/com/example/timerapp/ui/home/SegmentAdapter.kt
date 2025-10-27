package com.example.timerapp.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.timerapp.databinding.ItemSegmentBinding
import com.example.timerapp.model.Segment

class SegmentAdapter(private var segments: List<Segment>) : RecyclerView.Adapter<SegmentAdapter.SegmentViewHolder>() {

    class SegmentViewHolder(private val binding: ItemSegmentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(segment: Segment) {
            binding.textSegmentName.text = segment.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentViewHolder {
        val binding = ItemSegmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SegmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SegmentViewHolder, position: Int) {
        holder.bind(segments[position])
    }

    override fun getItemCount(): Int = segments.size

    fun updateSegments(newSegments: List<Segment>) {
        segments = newSegments
        notifyDataSetChanged()
    }
}