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
import com.timerapp.databinding.FragmentAddSegmentBinding
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

        // Load existing trigger configuration if in edit mode
        val existingTriggerType = arguments?.getSerializable("trigger_type") as? TriggerType
        if (existingTriggerType != null) {
            selectedTriggerType = existingTriggerType
            when (existingTriggerType) {
                TriggerType.MANUAL -> {
                    val delay = arguments?.getInt("trigger_delay", 0) ?: 0
                    // Will be set in setupTriggerUI after binding is available
                }
                TriggerType.DATETIME -> {
                    selectedDateTime =
                            arguments?.getSerializable("trigger_datetime") as? LocalDateTime
                    // Will be set in setupTriggerUI after binding is available
                }
            }
        }

        // Check if we're in edit mode
        isEditMode = segmentName != null && segmentIndex >= 0

        // Set up UI based on mode
        if (isEditMode) {
            binding.textTitle.text = "Edit Segment"
            binding.editTextSegmentName.setText(segmentName)
            binding.buttonSave.text = "Update"
            binding.buttonDelete.visibility = View.VISIBLE
        } else {
            binding.textTitle.text = "Add New Segment"
            binding.buttonSave.text = "Save"
            binding.buttonDelete.visibility = View.GONE
        }

        setupTriggerUI()

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
                    binding.textInputLayoutDelay.visibility = View.VISIBLE
                    binding.layoutDatetimeTrigger.visibility = View.GONE
                }
                binding.radioDatetimeTrigger.id -> {
                    selectedTriggerType = TriggerType.DATETIME
                    binding.textInputLayoutDelay.visibility = View.GONE
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
                val isEnabled = arguments?.getBoolean("trigger_enabled", false) ?: false
                binding.switchTriggerEnabled.isChecked = isEnabled
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
                    TriggerConfig.DateTime(
                            time = selectedDateTime!!,
                            isEnabled = binding.switchTriggerEnabled.isChecked
                    )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
