package com.timerapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timerapp.databinding.ItemSegmentBinding
import com.timerapp.model.Segment
import com.timerapp.model.TriggerConfig

class SegmentAdapter(
        private var segments: List<Segment>,
        private val onItemClick: (segment: Segment, position: Int) -> Unit,
        private val onExecuteManual: (segment: Segment, position: Int) -> Unit,
        private val onToggleDateTime: (segment: Segment, position: Int, isEnabled: Boolean) -> Unit
) : RecyclerView.Adapter<SegmentAdapter.SegmentViewHolder>() {

    class SegmentViewHolder(
            private val binding: ItemSegmentBinding,
            private val onItemClick: (segment: Segment, position: Int) -> Unit,
            private val onExecuteManual: (segment: Segment, position: Int) -> Unit,
            private val onToggleDateTime:
                    (segment: Segment, position: Int, isEnabled: Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(segment: Segment, position: Int) {
            binding.textSegmentName.text = segment.name

            // Configure UI based on trigger type
            when (val config = segment.triggerConfig) {
                is TriggerConfig.Manual -> {
                    // Show play button, hide toggle
                    binding.buttonExecuteManual.visibility = View.VISIBLE
                    binding.switchDatetimeEnabled.visibility = View.GONE

                    // Set up play button click listener
                    binding.buttonExecuteManual.setOnClickListener {
                        onExecuteManual(segment, position)
                    }
                }
                is TriggerConfig.DateTime -> {
                    // Show toggle, hide play button
                    binding.buttonExecuteManual.visibility = View.GONE
                    binding.switchDatetimeEnabled.visibility = View.VISIBLE

                    // Set toggle state
                    binding.switchDatetimeEnabled.isChecked = config.isEnabled

                    // Set up toggle listener
                    binding.switchDatetimeEnabled.setOnCheckedChangeListener { _, isChecked ->
                        onToggleDateTime(segment, position, isChecked)
                    }
                }
            }

            // Set click listener for the entire item (to edit)
            binding.root.setOnClickListener { onItemClick(segment, position) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SegmentViewHolder {
        val binding = ItemSegmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SegmentViewHolder(binding, onItemClick, onExecuteManual, onToggleDateTime)
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
