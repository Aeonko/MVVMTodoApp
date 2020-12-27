package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nemanjamiseljic.mvvmtodoapp.R
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TasksFragment: Fragment(R.layout.fragment_task) {

    private val viewModel: TasksViewModel by viewModels()
}