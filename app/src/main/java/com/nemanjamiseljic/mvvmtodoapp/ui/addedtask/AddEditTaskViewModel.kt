package com.nemanjamiseljic.mvvmtodoapp.ui.addedtask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nemanjamiseljic.mvvmtodoapp.ADD_TASK_RESULT_OK
import com.nemanjamiseljic.mvvmtodoapp.EDIT_TASK_RESULT_OK
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AddEditTaskViewModel @ViewModelInject constructor(
    private val taskDao: TaskDao,
    @Assisted private val state: SavedStateHandle /**SavedStateHandle is way of getting fragment savedinstance state into view model
                                                    *...with it we can make sure we don't lose data on process death
                                                    *...when app comes back from it reads data from here
                                                    *...passed arguments to fragment can be read from it instead of needing arguments call in fragment  **/
): ViewModel() {


    val task = state.get<Task>("task")

    var taskName = state.get<String>("taskName")?: task?.name?: ""
        set(value) {            /**saves values to save instance state (SavedStateHandle)**/
            field = value
            state.set("taskName",value)
        }

    var taskImportance:Boolean = state.get<Boolean>("taskImportance")?: task?.important?: false
        set(value) {            /**saves values to save instance state (SavedStateHandle)**/
            field = value
            state.set("taskImportance",value)
        }


    private val addEditTaskChannel = Channel<AddEditTaskEvent>()
    val addEditTaskEven = addEditTaskChannel.receiveAsFlow()


    fun onSaveClick() {
        if (taskName.isBlank()) {
            // show invalid input message
            showInvalidInputMessage("Name cannot be empty")
            return /**If it is blank return and don't execute rest of the code bellow**/
        }

        if (task != null) {
            // Updates old task
                /**Passed task from TasksFragment is not null or empty
                 * ...it means that we are updating existing task**/
            val updatedTask = task.copy(name = taskName,important = taskImportance)
            updateTask(updatedTask)
        }else{
            //Creates new task
            /**Passed task is null or empty
             * ...this means that we are creating new task
             */
            val newTask = Task(name = taskName,important = taskImportance)
            createTask(newTask)
        }
    }

    private fun createTask(task: Task) = viewModelScope.launch {
        taskDao.insert(task)
        //navigate back after it is inserted
        addEditTaskChannel.send(AddEditTaskEvent.NavigateBackWithResult(ADD_TASK_RESULT_OK))
    }
    private fun updateTask(task: Task) = viewModelScope.launch {
        taskDao.update(task)
        //navigate back after it is updated
        addEditTaskChannel.send(AddEditTaskEvent.NavigateBackWithResult(EDIT_TASK_RESULT_OK))
    }

    private fun showInvalidInputMessage(text: String) = viewModelScope.launch {
        addEditTaskChannel.send(AddEditTaskEvent.ShowInvalidInputMessage(text))
    }

    sealed class AddEditTaskEvent{
        data class ShowInvalidInputMessage(val msg: String): AddEditTaskEvent()
        data class NavigateBackWithResult(val result: Int): AddEditTaskEvent()
    }

}