package com.nemanjamiseljic.mvvmtodoapp.ui.deleteallcompleted

import androidx.lifecycle.ViewModel
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import com.nemanjamiseljic.mvvmtodoapp.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class DeleteAllCompletedViewModel @Inject constructor(
        private val taskDao: TaskDao,
        @AppModule.ApplicationScope private val applicationScope: CoroutineScope
): ViewModel() {

    fun onConfirmClick() = applicationScope.launch {
        taskDao.deleteCompletedTasks()
    }         /**Launches application scope in order to delete all completed
             *...if we used viewModelScope coroutine would stop when dialog is dismissed
             *...with application scope we make sure deletion process is executed as long as app runs
                * **/
}
