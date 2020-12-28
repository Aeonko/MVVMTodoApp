package com.nemanjamiseljic.mvvmtodoapp.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.nemanjamiseljic.mvvmtodoapp.ui.tasks.SortOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    fun getTasks(query: String,sortOrder: SortOrder, hideCompleted: Boolean): Flow<List<Task>> =
        when(sortOrder ){
            SortOrder.BY_DATE->{
                getTasksSortedByDateCreated(query,hideCompleted)
            }
            SortOrder.BY_NAME->{
                getTasksSortedByName(query,hideCompleted)
            }
        }

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%'|| :searchQuery ||'%' ORDER By important DESC, name")
    fun getTasksSortedByName(searchQuery: String,hideCompleted: Boolean): Flow<List<Task>>

    @Query("SELECT * FROM task_table WHERE (completed != :hideCompleted OR completed = 0) AND name LIKE '%'|| :searchQuery ||'%' ORDER By important DESC, created")
    fun getTasksSortedByDateCreated(searchQuery: String,hideCompleted: Boolean): Flow<List<Task>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

}