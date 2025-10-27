package com.example.timerapp.ui.addsegment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.timerapp.R
import com.example.timerapp.databinding.FragmentAddSegmentBinding

class AddSegmentFragment : Fragment() {

    private var _binding: FragmentAddSegmentBinding? = null
    private val binding get() = _binding!!

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

        binding.buttonSave.setOnClickListener {
            val segmentName = binding.editTextSegmentName.text.toString().trim()
            
            if (segmentName.isEmpty()) {
                binding.textInputLayoutName.error = "Please enter a segment name"
                return@setOnClickListener
            }

            // Clear any previous error
            binding.textInputLayoutName.error = null

            // Create a bundle to pass the segment name back
            val result = Bundle().apply {
                putString("segment_name", segmentName)
            }
            
            // Set the result for the parent fragment to receive
            parentFragmentManager.setFragmentResult("add_segment_result", result)
            
            // Navigate back
            findNavController().navigateUp()
        }

        binding.buttonCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        // Focus on the EditText when the fragment opens
        binding.editTextSegmentName.requestFocus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}