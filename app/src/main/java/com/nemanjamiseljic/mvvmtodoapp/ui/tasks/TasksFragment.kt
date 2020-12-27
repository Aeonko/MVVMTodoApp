package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.nemanjamiseljic.mvvmtodoapp.R
import com.nemanjamiseljic.mvvmtodoapp.databinding.FragmentTaskBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragment_task) {

    /**Gets instance of viewmodel**/
    private val viewModel: TasksViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentTaskBinding.bind(view)

        val tasksAdapter = TasksAdapter()


        binding.apply {
            recyclerViewTasks.apply {
                adapter = tasksAdapter
                layoutManager = LinearLayoutManager(requireContext())
                setHasFixedSize(true)
            }
        }

        viewModel.tasks.observe(viewLifecycleOwner,{
            tasksAdapter.submitList(it)
        })
    }
}