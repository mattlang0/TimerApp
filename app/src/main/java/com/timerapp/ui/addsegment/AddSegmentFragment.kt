package com.timerapp.ui.addsegment

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.textfield.TextInputEditText
import com.timerapp.databinding.FragmentAddSegmentBinding
import com.timerapp.model.Effect.Effect
import com.timerapp.model.Effect.PauseEffect
import com.timerapp.model.Effect.VibrateEffect
import com.timerapp.model.EffectType
import com.timerapp.model.TriggerConfig
import com.timerapp.model.TriggerType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class AddSegmentFragment : Fragment() {

    private var _binding: FragmentAddSegmentBinding? = null
    private val binding
        get() = _binding!!

    private var isEditMode = false
    private var segmentName: String? = null
    private var segmentIndex: Int = -1

    // Trigger-related state
    private var selectedTriggerType = TriggerType.MANUAL
    private var selectedDateTime: LocalDateTime? = null
    private val dateTimeFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy 'at' hh:mm a")

    // Effects-related state
    private val effectsList = mutableListOf<Effect>()
    private lateinit var effectAdapter: EffectAdapter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddSegmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get arguments
        segmentName = arguments?.getString("segmentName")
        segmentIndex = arguments?.getInt("segmentIndex", -1) ?: -1

        // Check if we're in edit mode
        isEditMode = segmentName != null && segmentIndex >= 0

        // Load existing trigger configuration if in edit mode
        val existingTriggerType = arguments?.getSerializable("trigger_type") as? TriggerType
        if (existingTriggerType != null) {
            selectedTriggerType = existingTriggerType
            when (existingTriggerType) {
                TriggerType.MANUAL -> {
                    // Delay will be loaded and set in setupTriggerUI after binding is available
                }
                TriggerType.DATETIME -> {
                    // Load the scheduled date/time from arguments
                    selectedDateTime =
                            arguments?.getSerializable("trigger_datetime") as? LocalDateTime
                }
            }
        }

        // Load existing effects if in edit mode
        val effectsCount = arguments?.getInt("effects_count", 0) ?: 0
        for (i in 0 until effectsCount) {
            val effectType = arguments?.getSerializable("effect_${i}_type") as? EffectType
            when (effectType) {
                EffectType.VIBRATE -> {
                    effectsList.add(VibrateEffect())
                }
                EffectType.PAUSE -> {
                    val duration = arguments?.getInt("effect_${i}_duration", 5) ?: 5
                    effectsList.add(PauseEffect(duration))
                }
                null -> {}
            }
        }

        // Set up UI based on mode
        if (isEditMode) {
            binding.editTextSegmentName.setText(segmentName)
            binding.buttonSave.text = "Update"
            binding.buttonDelete.visibility = View.VISIBLE
        } else {
            binding.buttonSave.text = "Save"
            binding.buttonDelete.visibility = View.GONE
        }

        setupTriggerUI()
        setupEffectsUI()

        binding.buttonSave.setOnClickListener {
            val segmentName = binding.editTextSegmentName.text.toString().trim()

            if (segmentName.isEmpty()) {
                binding.textInputLayoutName.error = "Please enter a segment name"
                return@setOnClickListener
            }

            // Validate trigger configuration
            val triggerConfig = getTriggerConfig()
            if (triggerConfig == null) {
                // Show error if DateTime trigger is selected but no date/time is set
                if (selectedTriggerType == TriggerType.DATETIME) {
                    AlertDialog.Builder(requireContext())
                            .setTitle("Missing Date/Time")
                            .setMessage("Please select a date and time for the scheduled trigger.")
                            .setPositiveButton("OK", null)
                            .show()
                    return@setOnClickListener
                }
            }

            // Clear any previous error
            binding.textInputLayoutName.error = null

            // Create a bundle to pass the result back
            val result =
                    Bundle().apply {
                        putString("segment_name", segmentName)
                        putBoolean("is_edit_mode", isEditMode)
                        if (isEditMode) {
                            putInt("segment_index", segmentIndex)
                        }
                        // Add trigger configuration
                        putSerializable("trigger_type", selectedTriggerType)
                        when (val config = triggerConfig) {
                            is TriggerConfig.Manual -> {
                                putInt("trigger_delay", config.delay)
                            }
                            is TriggerConfig.DateTime -> {
                                putSerializable("trigger_datetime", config.time)
                                putBoolean("trigger_enabled", config.isEnabled)
                            }
                            null -> {} // Should not happen due to validation above
                        }
                        // Add effects array
                        putInt("effects_count", effectsList.size)
                        effectsList.forEachIndexed { index, effect ->
                            when (effect) {
                                is VibrateEffect -> {
                                    putSerializable("effect_${index}_type", EffectType.VIBRATE)
                                }
                                is PauseEffect -> {
                                    putSerializable("effect_${index}_type", EffectType.PAUSE)
                                    putInt("effect_${index}_duration", effect.duration)
                                }
                            }
                        }
                    }

            // Set the result for the parent fragment to receive
            val resultKey = if (isEditMode) "edit_segment_result" else "add_segment_result"
            parentFragmentManager.setFragmentResult(resultKey, result)

            // Navigate back
            findNavController().navigateUp()
        }

        binding.buttonCancel.setOnClickListener { findNavController().navigateUp() }

        binding.buttonDelete.setOnClickListener { showDeleteConfirmationDialog() }

        // Focus on the EditText when the fragment opens
        binding.editTextSegmentName.requestFocus()

        // If editing, select all text for easy replacement
        if (isEditMode) {
            binding.editTextSegmentName.selectAll()
        }
    }

    private fun setupTriggerUI() {
        // Set up radio button listeners
        binding.radioGroupTriggerType.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                binding.radioManualTrigger.id -> {
                    selectedTriggerType = TriggerType.MANUAL
                    binding.layoutManualTrigger.visibility = View.VISIBLE
                    binding.layoutDatetimeTrigger.visibility = View.GONE
                }
                binding.radioDatetimeTrigger.id -> {
                    selectedTriggerType = TriggerType.DATETIME
                    binding.layoutManualTrigger.visibility = View.GONE
                    binding.layoutDatetimeTrigger.visibility = View.VISIBLE
                }
            }
        }

        // Set up date picker
        binding.buttonSelectDate.setOnClickListener { showDatePicker() }

        // Set up time picker
        binding.buttonSelectTime.setOnClickListener { showTimePicker() }

        // Populate UI with existing trigger data if in edit mode
        when (selectedTriggerType) {
            TriggerType.MANUAL -> {
                binding.radioManualTrigger.isChecked = true
                val delay = arguments?.getInt("trigger_delay", 0) ?: 0
                binding.editTextDelay.setText(delay.toString())
            }
            TriggerType.DATETIME -> {
                binding.radioDatetimeTrigger.isChecked = true
                updateDateTimeDisplay()
            }
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        if (selectedDateTime != null) {
            calendar.set(
                    selectedDateTime!!.year,
                    selectedDateTime!!.monthValue - 1,
                    selectedDateTime!!.dayOfMonth
            )
        }

        DatePickerDialog(
                        requireContext(),
                        { _, year, month, dayOfMonth ->
                            // Update selected date, preserving time if already set
                            val time =
                                    selectedDateTime?.toLocalTime()
                                            ?: LocalDateTime.now().toLocalTime()
                            selectedDateTime =
                                    LocalDateTime.of(
                                            year,
                                            month + 1,
                                            dayOfMonth,
                                            time.hour,
                                            time.minute
                                    )
                            updateDateTimeDisplay()
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                )
                .show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        if (selectedDateTime != null) {
            calendar.set(Calendar.HOUR_OF_DAY, selectedDateTime!!.hour)
            calendar.set(Calendar.MINUTE, selectedDateTime!!.minute)
        }

        TimePickerDialog(
                        requireContext(),
                        { _, hourOfDay, minute ->
                            // Update selected time, preserving date if already set
                            val date =
                                    selectedDateTime?.toLocalDate()
                                            ?: LocalDateTime.now().toLocalDate()
                            selectedDateTime =
                                    LocalDateTime.of(
                                            date,
                                            java.time.LocalTime.of(hourOfDay, minute)
                                    )
                            updateDateTimeDisplay()
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        false
                )
                .show()
    }

    private fun updateDateTimeDisplay() {
        if (selectedDateTime != null) {
            binding.textSelectedDatetime.text = selectedDateTime!!.format(dateTimeFormatter)
        } else {
            binding.textSelectedDatetime.text = "No date/time selected"
        }
    }

    private fun getTriggerConfig(): TriggerConfig? {
        return when (selectedTriggerType) {
            TriggerType.MANUAL -> {
                val delay = binding.editTextDelay.text.toString().toIntOrNull() ?: 0
                TriggerConfig.Manual(delay)
            }
            TriggerType.DATETIME -> {
                if (selectedDateTime == null) {
                    null
                } else {
                    // Preserve existing enabled state if in edit mode, otherwise default to false
                    val triggerEnabled =
                            if (isEditMode) {
                                arguments?.getBoolean("trigger_enabled", false) ?: false
                            } else {
                                false
                            }
                    TriggerConfig.DateTime(time = selectedDateTime!!, isEnabled = triggerEnabled)
                }
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
                .setTitle("Delete Segment")
                .setMessage(
                        "Are you sure you want to delete \"$segmentName\"? This action cannot be undone."
                )
                .setPositiveButton("Delete") { _, _ ->
                    // Create a bundle to pass the delete result back
                    val result =
                            Bundle().apply {
                                putBoolean("is_delete", true)
                                putInt("segment_index", segmentIndex)
                            }

                    // Set the result for the parent fragment to receive
                    parentFragmentManager.setFragmentResult("delete_segment_result", result)

                    // Navigate back
                    findNavController().navigateUp()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun setupEffectsUI() {
        // Set up RecyclerView
        effectAdapter =
                EffectAdapter(effectsList) { position ->
                    // Delete effect
                    effectsList.removeAt(position)
                    effectAdapter.notifyItemRemoved(position)
                    updateEffectsEmptyState()
                }

        binding.recyclerViewEffects.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = effectAdapter
        }

        // Add Effect button
        binding.buttonAddEffect.setOnClickListener { showEffectTypeSelectionDialog() }

        updateEffectsEmptyState()
    }

    private fun showEffectTypeSelectionDialog() {
        val effectTypes = arrayOf("Vibrate", "Pause")

        AlertDialog.Builder(requireContext())
                .setTitle("Select Effect Type")
                .setItems(effectTypes) { _, which ->
                    when (which) {
                        0 -> addVibrateEffect()
                        1 -> showPauseDurationDialog()
                    }
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun addVibrateEffect() {
        effectsList.add(VibrateEffect())
        effectAdapter.notifyItemInserted(effectsList.size - 1)
        updateEffectsEmptyState()
    }

    private fun showPauseDurationDialog() {
        val input =
                TextInputEditText(requireContext()).apply {
                    inputType = android.text.InputType.TYPE_CLASS_NUMBER
                    hint = "Duration (seconds)"
                    setText("5")
                }

        val container =
                android.widget.FrameLayout(requireContext()).apply {
                    setPadding(50, 20, 50, 0)
                    addView(input)
                }

        AlertDialog.Builder(requireContext())
                .setTitle("Pause Duration")
                .setView(container)
                .setPositiveButton("Add") { _, _ ->
                    val duration = input.text.toString().toIntOrNull() ?: 5
                    effectsList.add(PauseEffect(duration))
                    effectAdapter.notifyItemInserted(effectsList.size - 1)
                    updateEffectsEmptyState()
                }
                .setNegativeButton("Cancel", null)
                .show()
    }

    private fun updateEffectsEmptyState() {
        if (effectsList.isEmpty()) {
            binding.textEffectsEmpty.visibility = View.VISIBLE
            binding.recyclerViewEffects.visibility = View.GONE
        } else {
            binding.textEffectsEmpty.visibility = View.GONE
            binding.recyclerViewEffects.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
