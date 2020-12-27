package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import androidx.hilt.lifecycle.ViewModelInject

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao
): ViewModel() {
}