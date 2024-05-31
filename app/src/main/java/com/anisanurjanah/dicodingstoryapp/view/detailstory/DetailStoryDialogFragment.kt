package com.anisanurjanah.dicodingstoryapp.view.detailstory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import com.anisanurjanah.dicodingstoryapp.R
import com.anisanurjanah.dicodingstoryapp.data.remote.response.StoryItem
import com.anisanurjanah.dicodingstoryapp.databinding.FragmentDetailStoryDialogBinding
import com.anisanurjanah.dicodingstoryapp.utils.withDateFormat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class DetailStoryDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentDetailStoryDialogBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailStoryDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        @Suppress("DEPRECATION")
        val storyItem = arguments?.getParcelable<StoryItem>(ARG_STORY_ITEM)
        if (storyItem != null) {
            setupDetailStory(storyItem)
        } else {
            showToast(getString(R.string.failed_to_load_data))
        }
    }

    private fun setupDetailStory(items: StoryItem) {
        binding.apply {
            storyName.text = items.name ?: getString(R.string.not_available)
            storyDate.text = items.createdAt?.withDateFormat() ?: getString(R.string.not_available)
            storyDescription.text = items.description ?: getString(R.string.not_available)
            Glide.with(this@DetailStoryDialogFragment)
                .load(items.photoUrl)
                .centerCrop()
                .into(storyImage)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STORY_ITEM = "arg_story_item"

        fun newInstance(storyItem: StoryItem): DetailStoryDialogFragment {
            val fragment = DetailStoryDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_STORY_ITEM, storyItem)
            fragment.arguments = args
            return fragment
        }
    }
}