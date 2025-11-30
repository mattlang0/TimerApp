package com.timerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.timerapp.R
import com.timerapp.databinding.FragmentHomeBinding
import com.timerapp.model.Effect.Effect
import com.timerapp.model.Effect.PauseEffect
import com.timerapp.model.Effect.VibrateEffect
import com.timerapp.model.EffectType
import com.timerapp.model.TriggerConfig
import com.timerapp.model.TriggerType
import java.time.LocalDateTime

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding
        get() = _binding!!

    private lateinit var segmentAdapter: SegmentAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        segmentAdapter =
                SegmentAdapter(emptyList()) { segment, position ->
                    // Navigate to edit mode when segment is clicked
                    val bundle =
                            Bundle().apply {
                                putString("title", "Edit Segment")
                                putString("segmentName", segment.name)
                                putInt("segmentIndex", position)
                                // Pass trigger configuration
                                putSerializable(
                                        "trigger_type",
                                        when (segment.triggerConfig) {
                                            is TriggerConfig.Manual -> TriggerType.MANUAL
                                            is TriggerConfig.DateTime -> TriggerType.DATETIME
                                        }
                                )
                                when (val config = segment.triggerConfig) {
                                    is TriggerConfig.Manual -> {
                                        putInt("trigger_delay", config.delay)
                                    }
                                    is TriggerConfig.DateTime -> {
                                        putSerializable("trigger_datetime", config.time)
                                        putBoolean("trigger_enabled", config.isEnabled)
                                    }
                                }
                                // Pass effects
                                putInt("effects_count", segment.effects.size)
                                segment.effects.forEachIndexed { index, effect ->
                                    when (effect) {
                                        is VibrateEffect -> {
                                            putSerializable(
                                                    "effect_${index}_type",
                                                    EffectType.VIBRATE
                                            )
                                        }
                                        is PauseEffect -> {
                                            putSerializable(
                                                    "effect_${index}_type",
                                                    EffectType.PAUSE
                                            )
                                            putInt("effect_${index}_duration", effect.duration)
                                        }
                                    }
                                }
                            }
                    findNavController().navigate(R.id.action_nav_home_to_addSegmentFragment, bundle)
                }
        binding.recyclerViewSegments.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = segmentAdapter
        }

        // Observe segments list
        homeViewModel.segments.observe(viewLifecycleOwner) { segments ->
            segmentAdapter.updateSegments(segments)

            // Show/hide empty state
            if (segments.isEmpty()) {
                binding.recyclerViewSegments.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerViewSegments.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
            }
        }

        // Listen for result from AddSegmentFragment (add mode)
        parentFragmentManager.setFragmentResultListener("add_segment_result", this) { _, bundle ->
            val segmentName = bundle.getString("segment_name")
            val triggerConfig = extractTriggerConfigFromBundle(bundle)
            val effects = extractEffectsFromBundle(bundle)

            segmentName?.let {
                homeViewModel.addSegment(
                        com.timerapp.model.Segment(
                                name = it,
                                effects = effects,
                                triggerConfig = triggerConfig
                        )
                )
            }
        }

        // Listen for result from AddSegmentFragment (edit mode)
        parentFragmentManager.setFragmentResultListener("edit_segment_result", this) { _, bundle ->
            val segmentName = bundle.getString("segment_name")
            val segmentIndex = bundle.getInt("segment_index", -1)
            val triggerConfig = extractTriggerConfigFromBundle(bundle)
            val effects = extractEffectsFromBundle(bundle)

            if (segmentName != null && segmentIndex >= 0) {
                homeViewModel.updateSegment(
                        segmentIndex,
                        com.timerapp.model.Segment(
                                name = segmentName,
                                effects = effects,
                                triggerConfig = triggerConfig
                        )
                )
            }
        }

        // Listen for delete result from AddSegmentFragment
        parentFragmentManager.setFragmentResultListener("delete_segment_result", this) { _, bundle
            ->
            val isDelete = bundle.getBoolean("is_delete", false)
            val segmentIndex = bundle.getInt("segment_index", -1)

            if (isDelete && segmentIndex >= 0) {
                homeViewModel.deleteSegment(segmentIndex)
            }
        }

        return root
    }

    private fun extractTriggerConfigFromBundle(bundle: Bundle): TriggerConfig {
        val triggerType =
                bundle.getSerializable("trigger_type") as? TriggerType ?: TriggerType.MANUAL

        return when (triggerType) {
            TriggerType.MANUAL -> {
                val delay = bundle.getInt("trigger_delay", 0)
                TriggerConfig.Manual(delay = delay)
            }
            TriggerType.DATETIME -> {
                val dateTime = bundle.getSerializable("trigger_datetime") as? LocalDateTime
                val isEnabled = bundle.getBoolean("trigger_enabled", false)

                if (dateTime != null) {
                    TriggerConfig.DateTime(time = dateTime, isEnabled = isEnabled)
                } else {
                    // Fallback to Manual if DateTime is not properly set
                    TriggerConfig.Manual(delay = 0)
                }
            }
        }
    }

    private fun extractEffectsFromBundle(bundle: Bundle): Array<Effect> {
        val effectsCount = bundle.getInt("effects_count", 0)
        val effects = mutableListOf<Effect>()

        for (i in 0 until effectsCount) {
            val effectType = bundle.getSerializable("effect_${i}_type") as? EffectType
            when (effectType) {
                EffectType.VIBRATE -> {
                    effects.add(VibrateEffect())
                }
                EffectType.PAUSE -> {
                    val duration = bundle.getInt("effect_${i}_duration", 5)
                    effects.add(PauseEffect(duration))
                }
                null -> {} // Skip invalid effects
            }
        }

        return effects.toTypedArray()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
