package com.timerapp.ui.addsegment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timerapp.databinding.ItemEffectBinding
import com.timerapp.model.Effect.Effect
import com.timerapp.model.Effect.PauseEffect
import com.timerapp.model.Effect.VibrateEffect

class EffectAdapter(
        private var effects: MutableList<Effect>,
        private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<EffectAdapter.EffectViewHolder>() {

    class EffectViewHolder(val binding: ItemEffectBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EffectViewHolder {
        val binding = ItemEffectBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EffectViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EffectViewHolder, position: Int) {
        val effect = effects[position]

        when (effect) {
            is VibrateEffect -> {
                holder.binding.textEffectType.text = "Vibrate"
                holder.binding.textEffectConfig.visibility = View.GONE
            }
            is PauseEffect -> {
                holder.binding.textEffectType.text = "Pause"
                holder.binding.textEffectConfig.text = "Duration: ${effect.duration} seconds"
                holder.binding.textEffectConfig.visibility = View.VISIBLE
            }
        }

        holder.binding.buttonDeleteEffect.setOnClickListener { onDeleteClick(position) }
    }

    override fun getItemCount(): Int = effects.size

    fun updateEffects(newEffects: MutableList<Effect>) {
        effects = newEffects
        notifyDataSetChanged()
    }
}
