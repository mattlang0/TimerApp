package com.example.timerapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.timerapp.R
import com.example.timerapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var segmentAdapter: SegmentAdapter
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set up RecyclerView
        segmentAdapter = SegmentAdapter(emptyList()) { segment, position ->
            // Navigate to edit mode when segment is clicked
            val bundle = Bundle().apply {
                putString("segmentName", segment.name)
                putInt("segmentIndex", position)
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
            segmentName?.let {
                homeViewModel.addSegment(com.example.timerapp.model.Segment(it))
            }
        }

        // Listen for result from AddSegmentFragment (edit mode)
        parentFragmentManager.setFragmentResultListener("edit_segment_result", this) { _, bundle ->
            val segmentName = bundle.getString("segment_name")
            val segmentIndex = bundle.getInt("segment_index", -1)
            
            if (segmentName != null && segmentIndex >= 0) {
                homeViewModel.updateSegment(segmentIndex, com.example.timerapp.model.Segment(segmentName))
            }
        }

        // Listen for delete result from AddSegmentFragment
        parentFragmentManager.setFragmentResultListener("delete_segment_result", this) { _, bundle ->
            val isDelete = bundle.getBoolean("is_delete", false)
            val segmentIndex = bundle.getInt("segment_index", -1)
            
            if (isDelete && segmentIndex >= 0) {
                homeViewModel.deleteSegment(segmentIndex)
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}