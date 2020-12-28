package com.nemanjamiseljic.mvvmtodoapp.ui.tasks

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.nemanjamiseljic.mvvmtodoapp.data.PreferencesManager
import com.nemanjamiseljic.mvvmtodoapp.data.SortOrder
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class TasksViewModel @ViewModelInject constructor (
    private val taskDao: TaskDao,
    private val preferencesManager: PreferencesManager
): ViewModel() {

    val searchQuery = MutableStateFlow("")

    val preferencesFlow = preferencesManager.preferencesFlow

    private val taskEvenChannel = Channel<TasksEvent>()   /**Channel used to send data to Fragment ui trough channels
                                                            *...Channels have class that is sending data in our case TaskViewModel.kt
                                                            *...And they have class that is receiving data in our case TaskFragment.kt  **/

    val tasksEvent = taskEvenChannel.receiveAsFlow() /**Receives flow from taskEventChannel.
                                                        *...This could be done directly on @taskEvenChannel
                                                        *...but if it is done on @taskEvenChannel we can make error and try to write data to it
                                                        *...This way we are protecting it and if it is private we cant write data to it **/




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

    val tasks = taskFlow.asLiveData()
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
    // SEND DATA WITH CLASS Triple()

    fun onTaskSelected(task: Task){

    }
    fun onTaskCheckedChanged(task: Task, isChecked: Boolean)= viewModelScope.launch {
        taskDao.update(task.copy(completed = isChecked)) /**It copies original task just changes value of completed to new value**/
    }


    fun onTaskSwiped(task: Task)= viewModelScope.launch {
        /**Deletes task item from room database**/
        taskDao.deleteTask(task)
        taskEvenChannel.send(TasksEvent.ShowUndoDeleteTaskMessage(task))
        /**Sends data through channels and this can be received in
         * ...observers where we define channel receivers in this case TaskFragment.kt**/
    }
    fun onUndoDeleteClick(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        /**...task that was deleted is now again inserted as new object
         * ...to user this seams like same object is put back**/
    }

    sealed class TasksEvent{
        data class ShowUndoDeleteTaskMessage(val task: Task): TasksEvent()
    }/**Sealed classes are similar to enums. Main difference is that they can hold data**/
}
