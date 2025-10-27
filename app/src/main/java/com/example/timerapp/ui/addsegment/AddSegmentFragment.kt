package com.example.timerapp.ui.addsegment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.timerapp.databinding.FragmentAddSegmentBinding

class AddSegmentFragment : Fragment() {

    private var _binding: FragmentAddSegmentBinding? = null
    private val binding get() = _binding!!
    
    private var isEditMode = false
    private var segmentName: String? = null
    private var segmentIndex: Int = -1

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
        
        // Set up UI based on mode
        if (isEditMode) {
            binding.textTitle.text = "Edit Segment"
            binding.editTextSegmentName.setText(segmentName)
            binding.buttonSave.text = "Update"
        } else {
            binding.textTitle.text = "Add New Segment"
            binding.buttonSave.text = "Save"
        }

        binding.buttonSave.setOnClickListener {
            val segmentName = binding.editTextSegmentName.text.toString().trim()
            
            if (segmentName.isEmpty()) {
                binding.textInputLayoutName.error = "Please enter a segment name"
                return@setOnClickListener
            }

            // Clear any previous error
            binding.textInputLayoutName.error = null

            // Create a bundle to pass the result back
            val result = Bundle().apply {
                putString("segment_name", segmentName)
                putBoolean("is_edit_mode", isEditMode)
                if (isEditMode) {
                    putInt("segment_index", segmentIndex)
                }
            }
            
            // Set the result for the parent fragment to receive
            val resultKey = if (isEditMode) "edit_segment_result" else "add_segment_result"
            parentFragmentManager.setFragmentResult(resultKey, result)
            
            // Navigate back
            findNavController().navigateUp()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        // Focus on the EditText when the fragment opens
        binding.editTextSegmentName.requestFocus()
        
        // If editing, select all text for easy replacement
        if (isEditMode) {
            binding.editTextSegmentName.selectAll()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}