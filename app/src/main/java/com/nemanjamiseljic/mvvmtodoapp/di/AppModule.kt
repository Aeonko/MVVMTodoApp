package com.nemanjamiseljic.mvvmtodoapp.di

import android.app.Application
import androidx.room.Room
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDao
import com.nemanjamiseljic.mvvmtodoapp.data.TaskDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton                      /**Singleton makes sure that only one instance of database is created all the time app is running **/
    fun provideDatabase(            /**Provides database**/
        app:Application,            /**Gets context into app module, dagger hilt automatically knows how to create application context that is why we can pass it like this**/
        callback:TaskDatabase.Callback                                            /**TaskDatabase callback is inner class in TaskDatabase.kt**/
    ) = Room.databaseBuilder(app, TaskDatabase::class.java,"task_database")             /**Creates database**/
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

    @Provides
    fun provideTaskDao(                             /**Provides dao for working with table task**/
        db: TaskDatabase                            /**Gets instance of database created in method above "provideDatabase" **/
    ) = db.taskDao()


    @ApplicationScope // instance of annotation class ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob()) /** Provides application scope when needed to run methods like coroutines
                                                                    *...SupervisorJob() Means that if one coroutine fails others will not be affected.
     *                                                              *...it tells to coroutine if one child fails keep other running
     *                                                              *...Without this if one coroutine fails all are canceled**/




    @Retention(AnnotationRetention.RUNTIME)
    @Qualifier
    annotation class ApplicationScope /**If we want to have more then one scope provided by hilt we can annotate them with classes like this
                                        *..It means now we can specify which scope we want to use **/
}