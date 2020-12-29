package com.nemanjamiseljic.mvvmtodoapp.ui.addedtask

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.Task
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao

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


}