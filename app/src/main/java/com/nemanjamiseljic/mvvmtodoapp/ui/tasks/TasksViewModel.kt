package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao
): ViewModel() {

    val searchQuery = MutableStateFlow("")
    private val taskFlow = searchQuery.flatMapLatest {
        //When ever searchQuery is changed invoke this and get new tasks lists
        taskDao.getTasks(it)
    }
    val tasks = taskFlow.asLiveData()
}