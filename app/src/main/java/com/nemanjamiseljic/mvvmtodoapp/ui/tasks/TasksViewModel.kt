package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nemanjamiseljic.mvvmtodoapp.data.PreferencesManager
import com.nemanjamiseljic.mvvmtodoapp.data.SortOrder
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

//    val sortOrder = MutableStateFlow(SortOrder.BY_DATE)
//    val hideCompleted = MutableStateFlow(false)

    private val taskFlow = combine(
            /**Combines this three search queries into one later in the TaskDao interface**/
            /**For this can be created custom class to-do the same but for this here combine works perfectly just need to be careful of item order passed in combine **/
            searchQuery,
            preferencesFlow
    ){ query, filterPreferences ->
        Pair(query,filterPreferences) /**If there is data saved in DataStore class filter preferences have them that is why we implemented this class**/
         /**Pair is the class used to pass data as a dual of values**/
    }.flatMapLatest {(query,filterPreferences)->
        //When ever searchQuery, sort order or hide completed is changed invoke this and get new tasks lists
        taskDao.getTasks(query,filterPreferences.sortOrder,filterPreferences.hideCompleted)
    }

    fun onSortOrderSelected(sortOrder: SortOrder) = viewModelScope.launch {
        /**View model is called because we want coroutine that lives as long as current view**/
        preferencesManager.updateSortOrder(sortOrder)
    }
    fun onHideCompleted(hideCompleted: Boolean) = viewModelScope.launch {
        preferencesManager.updateHideCompleted(hideCompleted)
    }

//    private val taskFlow = combine(
//        /**Combines this three search queries into one later in the TaskDao interface**/
//        /**For this can be created custom class to-do the same but for this here combine works perfectly just need to be careful of item order passed in combine **/
//        searchQuery,
//        sortOrder,
//        hideCompleted
//    ){ query, sortOrder, hideCompleted ->
//        Triple(query,sortOrder,hideCompleted) /**Triple is the class used to pass data as a triad of values**/
//    }.flatMapLatest {(query,sortOrder,hideCompleted)->
//        //When ever searchQuery, sort order or hide completed is changed invoke this and get new tasks lists
//        taskDao.getTasks(query,sortOrder,hideCompleted)
//    }
    val tasks = taskFlow.asLiveData()
}
