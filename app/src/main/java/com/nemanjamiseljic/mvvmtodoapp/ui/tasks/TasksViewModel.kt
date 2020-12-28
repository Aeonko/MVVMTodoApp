package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
    val hideCompleted = MutableStateFlow(false)

    private val taskFlow = combine(
        /**Combines this three search queries into one later in the TaskDao interface**/
        searchQuery,
        sortOrder,
        hideCompleted
    ){ query, sortOrder, hideCompleted ->
        Triple(query,sortOrder,hideCompleted) /**Triple is the class used to pass data as a triad of values**/
    }.flatMapLatest {(query,sortOrder,hideCompleted)->
        //When ever searchQuery, sort order or hide completed is changed invoke this and get new tasks lists
        taskDao.getTasks(query,sortOrder,hideCompleted)
    }
    val tasks = taskFlow.asLiveData()
}
enum class SortOrder{
    BY_NAME,
    BY_DATE
}